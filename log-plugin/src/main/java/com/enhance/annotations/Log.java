package com.enhance.annotations;


import com.enhance.constant.LogConst;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>
 * 日志信息注解
 * </p>
 *
 * @author 龚梁钧 2019/06/27 18:18
 */
@Retention(RUNTIME)
@Target(ElementType.METHOD)
public @interface Log {
	// 操作类型
	LogConst.Action action() default LogConst.Action.NULL;

	// 对象类型
	String itemType() default "";

	// 对象ID
	String[] itemIds() default {};

	// 对象类型
	boolean printInfoLog() default false;


	// 如果是数组 是否打印出参大小，不打印对象值
	boolean printOutParamSize() default true;

	// 需要排除的入参
	Class[] excludeInParam() default {};

	// （其他）参数
	String[] param() default {};

}
