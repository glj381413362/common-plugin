package com.enhance.util;

import com.enhance.annotations.Log;
import java.util.Arrays;
import java.util.Stack;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.slf4j.profiler.Profiler;
import org.slf4j.profiler.ProfilerRegistry;
import org.slf4j.profiler.TimeInstrument;
import org.springframework.util.Assert;


/**
 * description: 日志打印工具类
 *
 * @author roman 2019/09/05 10:57
 */
@Slf4j
public class LogUtil {

  public final static ThreadLocal<Log> LOG_UTIL = new ThreadLocal<Log>();
  public final static ThreadLocal<Stack<String[]>> LOG_UTIL_CODES = new ThreadLocal<>();
  public final static ThreadLocal<Profiler> LOG_PROFILERS = new ThreadLocal<Profiler>();

  private final static String CODE = "code";
  private final static String MSG_FORMAT = "(%s:%s)";


  /**
   * 开始方法耗时统计,需要配合注解@Log使用
   *
   * @return void
   * @author gongliangjun 2020-06-01 11:26 AM
   */
  /*public static void startProfiler(String name) {
    if (isEnableProfiler()) {
      ProfilerRegistry profilerRegistry = ProfilerRegistry.getThreadContextInstance();
      Log logs = LOG_UTIL.get();
      String profilerName = logs.profilerName();
      Profiler childProfiler = profilerRegistry.get(profilerName);
      String names = new StringBuilder(profilerName).append("-->").append(name).toString();
      if (childProfiler != null) {
        childProfiler.start(names);
      }
    } else {
      log.warn("method startProfiler(name) 需要配合注解@Log使用,并且开启enableProfiler");
    }
  }*/

  /**
   * 打印方法耗时统计
   *
   * @return void
   * @author gongliangjun 2020-06-01 11:32 AM
   */
  public static void printProfiler() {
    Profiler profiler = LOG_PROFILERS.get();
    if (profiler != null) {
      TimeInstrument stop = profiler.stop();
      stop.print();
    }
  }

  /**
   * 打印自定义描述和多个code值 需要配合@log注解一起使用，MDC才能清空 一般在循环内使用,循环结束要调用clearMDC()
   *
   * @return void
   * @author gongliangjun 2019-10-30 11:04 AM
   */
  public static void printCode(String describe, String separator, String... values) {
    if (isEnableLogUtil()) {
      StringBuilder msg = new StringBuilder("(");
      if (StringUtils.isNotEmpty(describe)) {
        msg.append(describe).append(":");
      }
      if (StringUtils.isEmpty(separator)) {
        separator = " ";
      }
      String msgs = Arrays.stream(values).collect(Collectors.joining(separator));
      msg = msg.append(msgs).append(")");
      MDC.put(CODE, msg.toString());
      Stack<String[]> stack = LOG_UTIL_CODES.get();
      stack.peek()[0] = msg.toString();
      LOG_UTIL_CODES.set(stack);
    } else {
      log.warn("method printCode() 需要配合注解@Log使用");
    }
  }

  /**
   * 打印自定义描述和code值 需要配合@log注解一起使用，MDC才能清空 一般在循环内使用,循环结束要调用clearMDC()
   *
   * @return void
   * @author gongliangjun 2019-10-30 11:03 AM
   */
  public static void printCode(String describe, String value) {
    if (isEnableLogUtil()) {
      MDC.put(CODE, String.format(MSG_FORMAT, describe, value));
      Stack<String[]> stack = LOG_UTIL_CODES.get();
      stack.peek()[0] = String.format(MSG_FORMAT, describe, value);
      LOG_UTIL_CODES.set(stack);
    } else {
      log.warn("method printCode() 需要配合注解@Log使用");
    }
  }

  /**
   * 打印特定code值 需要配合@log注解一起使用，MDC才能清空 一般在循环内使用,循环结束要调用clearMDC()
   *
   * @return void
   * @author gongliangjun 2019-10-30 11:03 AM
   */
  public static void printCode(String value) {
    if (isEnableLogUtil()) {
      MDC.put(CODE, value);
      Stack<String[]> stack = LOG_UTIL_CODES.get();
      stack.peek()[0] = value;
      LOG_UTIL_CODES.set(stack);
    } else {
      log.warn("method printCode() 需要配合注解@Log使用");
    }
  }

  /**
   * 自定义模板打印日志 需要配合@log注解一起使用，MDC才能清空 一般在循环内使用,循环结束要调用clearMDC()
   *
   * @param template 和log使用方式一样，使用{}作为占位符
   * @param values 需要替换占位符{}的值
   * @return void
   * @author gongliangjun 2019-10-30 10:47 AM
   */
  public static void printCodeByTemplate(String template, Object... values) {
    if (isEnableLogUtil()) {
      String msgFormat = template.replace("{}", "%s");
      MDC.put(CODE, String.format(msgFormat, values));
      Stack<String[]> stack = LOG_UTIL_CODES.get();
      stack.peek()[0] = String.format(msgFormat, values);
      LOG_UTIL_CODES.set(stack);
    } else {
      log.warn("method printCodeByTemplate() 需要配合注解@Log使用");
    }
  }

  public static void clearMDC() {
    if (isEnableLogUtil()) {
      MDC.remove(CODE);
      Stack<String[]> stack = LOG_UTIL_CODES.get();
      stack.peek()[0] = "";
      LOG_UTIL_CODES.set(stack);
    } else {
      log.warn("method printCodeByTemplate() 需要配合注解@Log使用");
    }
  }

  private static boolean isEnableLogUtil() {
    Log logs = LOG_UTIL.get();
    return logs != null;
  }
/*
  private static boolean isEnableProfiler() {

    Log logs = LOG_UTIL.get();
    if (logs != null) {
      return logs.enableProfiler();
    } else {
      return false;
    }
  }*/
}
