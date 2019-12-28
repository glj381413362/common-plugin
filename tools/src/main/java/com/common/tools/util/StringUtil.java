package com.common.tools.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.helpers.MessageFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.StringTokenizer;


/**
 * <p>
 * 字符串工具类
 * </p>
 *
 * @author gongliangjun 2019/12/28 5:36 PM
 */
@Slf4j
public class StringUtil {

	/**
	 * 判断字符串是否相等，并且打印日志
	 *
	 * @param one
	 * @param two
	 * @param msg
	 * @return boolean
	 * @author gongliangjun 2019-12-25 5:50 PM
	 */
	public static boolean strEquals(Object one, Object two, String msg) {
		boolean res = Objects.equals(one, two);
		log.info(msg + " :[{}]", res);
		return res;
	}

	/**
	 * 判断字符串是否为空，并且打印日志
	 *
	 * @param str
	 * @param msg
	 * @return boolean
	 * @author gongliangjun 2019-12-25 5:50 PM
	 */
	public static boolean strIsBlank(String str, String msg) {
		boolean res = StringUtils.isBlank(str);
		log.info(msg + " :[{}]", res);
		return res;
	}

	/**
	 * 判断字符串是否不为空，并且打印日志
	 *
	 * @param str
	 * @param msg
	 * @return boolean
	 * @author gongliangjun 2019-12-25 5:50 PM
	 */
	public static boolean strNotBlank(String str, String msg) {
		boolean res = StringUtils.isNotBlank(str);
		log.info(msg + " :[{}]", res);
		return res;
	}

	/**
	 * url编码
	 *
	 * @param str
	 * @return
	 */
	public static String encodeUrl(String str) {
		String s = null;
		try {
			s = URLEncoder.encode(str, PluginConstants.UTF8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return s;
	}

	/**
	 * url解码
	 *
	 * @param str
	 * @return
	 */
	public static String decodeUrl(String str) {
		String s = null;
		try {
			s = URLDecoder.decode(str, PluginConstants.UTF8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s;
	}


	/**
	 * 字符串分隔 StringTokenizer效率是三种分隔方法中最快的
	 *
	 * @param str
	 * @param sign
	 * @return
	 */
	public static String[] split(String str, String sign) {
		if (str == null) {
			return new String[]{};
		}
		StringTokenizer token = new StringTokenizer(str, sign);
		String[] strArr = new String[token.countTokens()];
		int i = 0;
		while (token.hasMoreElements()) {
			strArr[i] = token.nextElement().toString();
			i++;
		}
		return strArr;
	}

	/**
	 * 字符串拼接
	 *
	 * @param sign
	 * @param strArr
	 * @return
	 */
	public static String joinStr(String sign, String... strArr) {
		Optional<String> optional = Arrays.stream(strArr).filter(Objects::nonNull
		).reduce((a, b) -> a + sign + b);
		return optional.orElse("");
	}

	/**
	 * @param e
	 * @return exception-string
	 * @author youlong.peng
	 * @description 将异常栈中的内容转化为String
	 */
	public static String exceptionString(final Exception e) {
		try {
			final StringWriter sw = new StringWriter();
			final PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return "exceptionString> " + sw.toString() + "\r\n";
		} catch (final Exception e2) {
			final String errorMsg = "Common exception util error occured :" + e2.getMessage();
			log.error(errorMsg, e);
			return errorMsg;
		}
	}

	/**
	 * 获取格式化后的消息,格式如:my name is {}, my age is {}
	 *
	 * @param message
	 * @param objects
	 * @return
	 */
	public static String getMessageFormat(String message, Object... objects) {
		if (message == null) {
			return message;
		}
		return MessageFormatter.arrayFormat(message, objects).getMessage();
	}
}
