package com.enhance.aspect;


import static com.enhance.util.LogUtil.LOG_PROFILERS;

import com.enhance.util.LogUtil;
import java.lang.reflect.Proxy;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.slf4j.profiler.TimeInstrument;
import org.springframework.stereotype.Component;


/**
 * description
 *
 * @author 龚梁钧 2019/06/27 18:27
 */
@Aspect
@Component
public class ProfilerAOP {


  @Pointcut("@annotation(com.enhance.annotations.EnableProfiler)")
  public void profilerPoint() {
  }

  @SneakyThrows
  @Around("profilerPoint()")
  public Object handlerLogMethod(ProceedingJoinPoint joinPoint) {
    Object result = null;
    Signature signature = joinPoint.getSignature();
    Object target = joinPoint.getTarget();
    Class<?> targetClass = target.getClass();
    Logger logger = LoggerFactory.getLogger(targetClass);
    StringBuilder methodName = new StringBuilder();
    if (target instanceof Proxy) {
      methodName.append(signature.getDeclaringTypeName())
          .append(".");
    }
    methodName.append(signature.getName());
    Profiler profiler = LOG_PROFILERS.get();
    boolean shoudPrint = false;
    try {
      if (profiler == null) {
        shoudPrint = true;
        LOG_PROFILERS.set(new Profiler(methodName.toString() + " CostTimeMontor"));
        profiler = LOG_PROFILERS.get();
        profiler.setLogger(logger);
        profiler.start(methodName.toString());
      } else {
        profiler.start(methodName.toString());
      }

      result = joinPoint.proceed();
    } catch (Exception e) {
      //如果有异常继续抛
      throw e;
    } finally {
      if (shoudPrint) {
        TimeInstrument timeInstrument = profiler.stop();
        timeInstrument.print();
        LOG_PROFILERS.remove();
      }
    }
    return result;
  }
}
