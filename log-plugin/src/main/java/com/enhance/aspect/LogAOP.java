package com.enhance.aspect;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.enhance.annotations.Log;
import com.enhance.constant.LogConst;
import com.enhance.core.service.FilterResultService;
import com.enhance.core.service.LogService;
import com.enhance.util.LogUtil;
import com.enhance.util.SPELUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.profiler.Profiler;
import org.slf4j.profiler.ProfilerRegistry;
import org.slf4j.profiler.TimeInstrument;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


/**
 * description
 *
 * @author 龚梁钧 2019/06/27 18:27
 */
@Aspect
@Component
public class LogAOP implements ApplicationContextAware {

  /**
   * logger
   */
  private static final Logger LOG = LoggerFactory.getLogger(LogAOP.class);
  private static final String LOG_JSON = "logjson";
  private static final String USER = "user";
  private static final String CODE = "code";
  private static final String DOT_NOTATION = ".";
  @Autowired
  private List<FilterResultService> filterResultServices;

  private LogService logService;

  @Pointcut("@annotation(com.enhance.annotations.Log)")
  public void logPoint() {
  }

  @SneakyThrows
  @Around("logPoint()")
  public Object handlerLogMethod(ProceedingJoinPoint joinPoint) {
    //
    //  得到方法上的注解
    // ------------------------------------------------------------------------------
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Log logAnnotation = signature.getMethod().getAnnotation(Log.class);
    //===============================================================================
    //  将log存到ThreadLocal中，在配合logutil使用
    //===============================================================================
    LogUtil.LOG_UTIL.set(logAnnotation);

    Stack<String[]> stack = LogUtil.LOG_UTIL_CODES.get();
    if (stack == null) {
      stack = new Stack<>();
    }
    stack.push(new String[]{"", ""});
    LogUtil.LOG_UTIL_CODES.set(stack);

    Profiler profiler = LogUtil.LOG_PROFILERS.get();
    boolean shoudPrint = profiler == null ? true : false;
    this.handBeforeMethodLog(joinPoint, logAnnotation);
    Object result = null;
    try {
      result = joinPoint.proceed();
    } catch (Exception e) {
      e.printStackTrace();
      //如果有异常继续抛
      throw e;
    } finally {
      try {
        profiler = LogUtil.LOG_PROFILERS.get();
        if (shoudPrint && profiler != null) {
          TimeInstrument timeInstrument = profiler.stop();
          timeInstrument.print();
          LogUtil.LOG_PROFILERS.remove();
          ProfilerRegistry profilerRegistry = ProfilerRegistry.getThreadContextInstance();
          profilerRegistry.clear();
        }
        this.handAfterMethodLog(joinPoint, logAnnotation, result);
        this.handleMDCValue();
      }catch (Exception e){
        LOG.error("finally error : {}",e);
      }

    }
    return result;
  }

  private void handleMDCValue() {
    Stack<String[]> stacks = LogUtil.LOG_UTIL_CODES.get();
    if (stacks != null) {
      //===============================================================================
      //  先对自己的code出栈
      //===============================================================================
      stacks.pop();
      if (!stacks.empty()) {
        //===============================================================================
        //  判断是否要把之前的数据存入MDC
        //===============================================================================
        String[] peek = stacks.peek();
        if (StringUtils.isNotEmpty(peek[0])) {
          MDC.put(CODE, peek[0]);
        }
        if (StringUtils.isNotEmpty(peek[1])) {
          MDC.put(LOG_JSON, peek[1]);
        }
      } else {
        // 因为Tomcat线程重用
        clearMDC(new String[]{CODE, USER, LOG_JSON});
        LogUtil.LOG_UTIL_CODES.remove();
        LogUtil.LOG_UTIL.remove();
      }
    } else {
      // 因为Tomcat线程重用
      clearMDC(new String[]{CODE, USER, LOG_JSON});
      LogUtil.LOG_UTIL_CODES.remove();
      LogUtil.LOG_UTIL.remove();
    }
  }

  /**
   * 打印调用方法后的日志
   *
   * @return void
   * @author 龚梁钧 2019-06-28 13:15
   */
  @Deprecated
  private void handAfterMethodLog(ProceedingJoinPoint joinPoint, Log logAnnotation, long startTime,
      Object result) {
    // 是否需要打印日志
    boolean printLog = logAnnotation.printInfoLog();
    // 如果是数组 是否打印出参大小，不打印对象值
    boolean printOutParamSize = logAnnotation.printOutParamSize();
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    StringBuffer endString = new StringBuffer();
    Object target = joinPoint.getTarget();
    Logger logger = LoggerFactory.getLogger(target.getClass());
    if (target instanceof Proxy) {
      endString.append(signature.getDeclaringTypeName())
          .append(".")
          .append(signature.getName());
    }
    endString.append(signature.getName());
    long handleTime = System.currentTimeMillis() - startTime;
    endString.append(" end ")
        .append("time consuming(")
        .append(handleTime)
        .append("ms)");
    if (logger.isDebugEnabled() || printLog) {
      if (result instanceof Collection && printOutParamSize) {
        logger.info(endString.append(" output parameters size:{}").toString(),
            Collection.class.cast(result).size());
        return;
      }
      String responseStr = null;
      try {
        if (CollectionUtils.isNotEmpty(filterResultServices)) {
          for (FilterResultService filterResultService : filterResultServices) {
            Optional filterResult = filterResultService.filterResult(result);
            // 返回null，说明不需要处理  返回Optional.empty(),说明处理了，处理结果为null
            if (filterResult != null) {
              LOG.debug("返回结果处理成功");
              if (filterResult.isPresent()) {
                Object fr = filterResult.get();
                if (fr instanceof Collection && printOutParamSize) {
                  logger.info(endString.append(" output parameters size:{}").toString(),
                      Collection.class.cast(fr).size());
                  return;
                } else {
                  responseStr = JSON.toJSONString(filterResult);
                }
                break;
              } else {
                LOG.debug("处理结果为空");
              }

            }
          }
        }
      } catch (Exception e) {
        LOG.warn("handAfterMethodLog error:{}", e);
      }
      if (StringUtils.isEmpty(responseStr)) {
        responseStr = result == null ? "void" : JSON.toJSONString(result);
      }
      endString.append("output parameters：").append(responseStr);
      if (printLog) {
        logger.info(endString.toString());
      } else {
        logger.debug(endString.toString());
      }
    } else {
      logger.info(endString.toString());
    }
  }

  /**
   * 打印调用方法后的日志
   *
   * @return void
   * @author 龚梁钧 2019-06-28 13:15
   */
  private void handAfterMethodLog(ProceedingJoinPoint joinPoint, Log logAnnotation, Object result) {
    // 是否需要打印日志
    boolean printLog = logAnnotation.printInfoLog();
    // 如果是数组 是否打印出参大小，不打印对象值
    boolean printOutParamSize = logAnnotation.printOutParamSize();
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    StringBuffer endString = new StringBuffer();
    Object target = joinPoint.getTarget();
    Logger logger = LoggerFactory.getLogger(target.getClass());
    if (target instanceof Proxy) {
      endString.append(signature.getDeclaringTypeName())
          .append(".")
          .append(signature.getName());
    }
    endString.append(signature.getName());

    endString.append(" end ");
    if (logger.isDebugEnabled() || printLog) {
      if (result instanceof Collection && printOutParamSize) {
        logger.info(endString.append(" output parameters size:{}").toString(),
            Collection.class.cast(result).size());
        return;
      }
      String responseStr = null;
      try {
        if (CollectionUtils.isNotEmpty(filterResultServices)) {
          for (FilterResultService filterResultService : filterResultServices) {
            Optional filterResult = filterResultService.filterResult(result);
            // 返回null，说明不需要处理  返回Optional.empty(),说明处理了，处理结果为null
            if (filterResult != null) {
              LOG.debug("返回结果处理成功");
              if (filterResult.isPresent()) {
                Object fr = filterResult.get();
                if (fr instanceof Collection && printOutParamSize) {
                  logger.info(endString.append(" output parameters size:{}").toString(),
                      Collection.class.cast(fr).size());
                  return;
                } else {
                  responseStr = JSON.toJSONString(filterResult);
                }
                break;
              } else {
                LOG.debug("处理结果为空");
              }

            }
          }
        }
      } catch (Exception e) {
        LOG.warn("handAfterMethodLog error:{}", e);
      }
      if (StringUtils.isEmpty(responseStr)) {
        responseStr = result == null ? "void" : JSON.toJSONString(result);
      }
      endString.append("output parameters：").append(responseStr);
      if (printLog) {
        logger.info(endString.toString());
      } else {
        logger.debug(endString.toString());
      }
    } else {
      logger.info(endString.toString());
    }
  }

  /**
   * 打印调用方法前的日志
   *
   * @return org.slf4j.Logger
   * @author 龚梁钧 2019-06-28 13:13
   */
  private Logger handBeforeMethodLog(ProceedingJoinPoint joinPoint, Log logAnnotation) {
    Class[] excludeInParam = logAnnotation.excludeInParam();
    String[] params = logAnnotation.param();
    Object target = joinPoint.getTarget();
    Class<?> targetClass = target.getClass();
    Signature signature = joinPoint.getSignature();
    // 是否需要打印日志
    boolean printLog = logAnnotation.printInfoLog();
    boolean enableProfiler = logAnnotation.enableProfiler();
    Logger logger = LoggerFactory.getLogger(targetClass);
    if (logger.isDebugEnabled() || printLog) {
      //===============================================================================
      //  保存action、itemType、itemId到MDC
      //===============================================================================
      saveLogInfo2MDC(joinPoint, logAnnotation);

      StringBuilder mesInfo = new StringBuilder();
      if (target instanceof Proxy) {
        mesInfo.append(signature.getDeclaringTypeName())
            .append(".");
      }
      mesInfo.append(signature.getName());
     String profilerName =  new StringBuilder(targetClass.getSimpleName()).append("#").append(mesInfo.toString()).toString();
      //===============================================================================
      //  方法调用耗时统计
      //===============================================================================
      if (enableProfiler) {
        Profiler profiler = LogUtil.LOG_PROFILERS.get();
        if (profiler == null) {
          profiler = new Profiler(mesInfo.toString());
          LogUtil.LOG_PROFILERS.set(profiler);
          // 在线程上下文的探查器注册表中注册此探查器
          ProfilerRegistry profilerRegistry = ProfilerRegistry.getThreadContextInstance();
          profiler.registerWith(profilerRegistry);
          profiler.setLogger(logger);
          profiler.start(profilerName);

        } else {
          try {
            //===============================================================================
            //  动态修改log注解的profilerName值
            //===============================================================================
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(logAnnotation);
            Field declaredField = invocationHandler.getClass().getDeclaredField("memberValues");
            declaredField.setAccessible(true);
            // 获取 memberValues
            Map memberValues = (Map) declaredField.get(invocationHandler);
            // 修改 profilerName 属性值
            memberValues.put("profilerName", profilerName);


            profiler.startNested(profilerName);
            // 获取注册的分析器
            ProfilerRegistry profilerRegistry = ProfilerRegistry.getThreadContextInstance();
            Profiler childProfiler = profilerRegistry.get(profilerName);
            if (childProfiler != null) {
              childProfiler.start(profilerName);
            }
          } catch (IllegalAccessException e) {
            LOG.warn("IllegalAccessException {}:",e);
          } catch (NoSuchFieldException e) {
            LOG.warn("NoSuchFieldException {}:",e);
          }
        }
      }
      mesInfo.append(" start. parameters :{}");
      //
      // 如果params大于0，说明存在用户想打印的参数
      // ------------------------------------------------------------------------------
      if (params.length > 0) {
        SPELUtil spel = new SPELUtil(joinPoint);
        JSONObject json = new JSONObject();
        for (String param : params) {
          // 其他参数，spel表达式
          json.put(param, spel.cacl(param));
        }
        if (printLog) {
          logger.info(mesInfo.toString(), json.toJSONString());
        } else {
          logger.debug(mesInfo.toString(), json.toJSONString());
        }
      } else {
        // 所在的类.方法
        String methodStr = this.getMethodParam(joinPoint, excludeInParam);
        if (printLog) {
          logger.info(mesInfo.toString(), methodStr);
        } else {
          logger.debug(mesInfo.toString(), methodStr);
        }
      }
    }
    return logger;
  }

  /**
   * 打印调用方法前的日志
   *
   * @return org.slf4j.Logger
   * @author 龚梁钧 2019-06-28 13:13
   */
 /* @Deprecated
  private Logger handBeforeMethodLog(ProceedingJoinPoint joinPoint, Log logAnnotation) {
    Class[] excludeInParam = logAnnotation.excludeInParam();
    String[] params = logAnnotation.param();
    Object target = joinPoint.getTarget();
    Class<?> targetClass = target.getClass();
    Signature signature = joinPoint.getSignature();
    // 是否需要打印日志
    boolean printLog = logAnnotation.printInfoLog();
    Logger logger = LoggerFactory.getLogger(targetClass);
    if (logger.isDebugEnabled() || printLog) {
      //===============================================================================
      //  保存action、itemType、itemId到MDC
      //===============================================================================
      saveLogInfo2MDC(joinPoint, logAnnotation);

      StringBuilder mesInfo = new StringBuilder();
      if (target instanceof Proxy) {
        mesInfo.append(signature.getDeclaringTypeName())
            .append(".");
      }
      mesInfo.append(signature.getName());
      mesInfo.append(" start. parameters :{}");
      //
      // 如果params大于0，说明存在用户想打印的参数
      // ------------------------------------------------------------------------------
      if (params.length > 0) {
        SPELUtil spel = new SPELUtil(joinPoint);
        JSONObject json = new JSONObject();
        for (String param : params) {
          // 其他参数，spel表达式
          json.put(param, spel.cacl(param));
        }
        if (printLog) {
          logger.info(mesInfo.toString(), json.toJSONString());
        } else {
          logger.debug(mesInfo.toString(), json.toJSONString());
        }
      } else {
        // 所在的类.方法
        String methodStr = this.getMethodParam(joinPoint, excludeInParam);
        if (printLog) {
          logger.info(mesInfo.toString(), methodStr);
        } else {
          logger.debug(mesInfo.toString(), methodStr);
        }
      }
    }
    return logger;
  }
*/
  private void saveLogInfo2MDC(ProceedingJoinPoint pjp, Log logAnnotation) {
    //===============================================================================
    //  保存用户信息到MDC
    //===============================================================================
    if (this.logService != null) {
      String userInfo = logService.getUserInfo();
      if (StringUtils.isNotEmpty(userInfo)) {
        MDC.put(USER, userInfo);
      }
    }
    JSONObject json = this.getLogJson(pjp, logAnnotation);
    if (!json.isEmpty()) {
      MDC.put(LOG_JSON, json.toJSONString());
      Stack<String[]> stack = LogUtil.LOG_UTIL_CODES.get();
      stack.peek()[1] = json.toJSONString();
      LogUtil.LOG_UTIL_CODES.set(stack);
    }
  }

  /**
   * 获取注解Log的值,构造成json，供sf4j使用
   *
   * @return com.alibaba.fastjson.JSONObject
   * @author 龚梁钧 2019-06-28 11:24
   */
  private JSONObject getLogJson(ProceedingJoinPoint pjp, Log logAnnotation) {
    LogConst.Action action = logAnnotation.action();
    String itemType = logAnnotation.itemType();
    String[] itemIds = logAnnotation.itemIds();
    SPELUtil spel = new SPELUtil(pjp);
    JSONObject json = new JSONObject();
    // 操作
    if (!LogConst.Action.NULL.equals(action)) {
      json.put("A", action.toString());
    }
    // 对象类型
    if (StringUtils.isNotEmpty(itemType)) {
      json.put("T", itemType);
    }
    // 对象类型
    if (itemIds.length > 0) {
      for (String itemId : itemIds) {
        if (itemId.contains(DOT_NOTATION)) {
          String substring = itemId.substring(itemId.indexOf(DOT_NOTATION) + 1);
          json.put(substring, spel.cacl(itemId));
        } else {
          json.put(itemId, spel.cacl(itemId));
        }
      }
    }
    return json;
  }

  private void clearMDC(String[] clears) {
    for (String clear : clears) {
      MDC.remove(clear);
    }
  }

  /**
   * 获取方法入参
   *
   * @return java.lang.String
   * @author 龚梁钧 2019-06-28 13:35
   */
  private String getMethodParam(ProceedingJoinPoint point, Class[] excludeInParam) {
    Object[] methodArgs = point.getArgs();
    Parameter[] parameters = ((MethodSignature) point.getSignature()).getMethod().getParameters();
    String requestStr;
    try {
      requestStr = logParam(parameters, methodArgs, excludeInParam);
    } catch (Exception e) {
      requestStr = "failed to get parameters";
    }
    return requestStr;
  }

  /**
   * 拼接入参
   *
   * @param excludeInParam 不需要拼接的参数
   * @return java.lang.String
   * @author 龚梁钧 2019-06-28 13:34
   */
  private String logParam(Parameter[] paramsArgsName, Object[] paramsArgsValue,
      Class[] excludeInParam) {
    if (ArrayUtils.isEmpty(paramsArgsName) || ArrayUtils.isEmpty(paramsArgsValue)) {
      return "";
    }
    StringBuffer buffer = new StringBuffer();
    Flag:
    for (int i = 0; i < paramsArgsValue.length; i++) {
      //参数名
      String name = paramsArgsName[i].getName();
      //参数值
      Object value = paramsArgsValue[i];
      //
      // 判断当前参数值是否属于excludeInParam，如果属于，则跳过不进行拼接
      // ------------------------------------------------------------------------------
      for (Class exclude : excludeInParam) {
        if (exclude.equals(value.getClass())) {
          continue Flag;
        }
      }
      buffer.append(name + "=");
      if (value instanceof String) {
        buffer.append(value + ",");
      } else {
        buffer.append(JSON.toJSONString(value) + ",");
      }
    }
    return buffer.toString();
  }


  @Override
  public void setApplicationContext(ApplicationContext applicationContext) {
    try {
      this.logService = applicationContext.getBean(LogService.class);
    } catch (BeansException e) {
      LOG.info("logService is null");
    }
  }
}
