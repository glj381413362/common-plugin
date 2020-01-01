package com.common.tools.util;

import lombok.extern.slf4j.Slf4j;

import static com.common.tools.util.StringUtil.strFormat;

/**
 * <p>
 *
 * </p>
 *
 * @author gongliangjun 2019/07/01 11:18
 */
@Slf4j
public class CommonUtil {

	/**
	 * 判断对象是否为空，并且打印日志
	 *
	 * @param object
	 * @param msg
	 * @return boolean
	 * @author gongliangjun 2019-12-25 5:50 PM
	 */
	public static boolean objIsNull(Object object, String msg) {
		boolean res = null == object;
		log.info(msg + " :[{}]", res);
		return res;
	}

	/**
	 * 判断对象是否为空，并且打印日志
	 *
	 * @param object
	 * @param msgT
	 * @param params
	 * @return boolean
	 * @author gongliangjun 2019-12-25 5:50 PM
	 */
	public static boolean objIsNull(Object object, String msgT, Object... params) {
		boolean res = null == object;
		log.info(strFormat(msgT, params) + " :[{}]", res);
		return res;
	}

	/**
	 * 判断对象是否不为空，并且打印日志
	 *
	 * @param object
	 * @param msg
	 * @return boolean
	 * @author gongliangjun 2019-12-25 5:50 PM
	 */
	public static boolean objIsNotNull(Object object, String msg) {
		boolean res = null != object;
		log.info(msg + " :[{}]", res);
		return res;
	}

	/**
	 * 判断对象是否不为空，并且打印日志
	 *
	 * @param object
	 * @param msgT
	 * @param params
	 * @return boolean
	 * @author gongliangjun 2019-12-25 5:50 PM
	 */
	public static boolean objIsNotNull(Object object, String msgT, Object... params) {
		boolean res = null != object;
		log.info(strFormat(msgT, params) + " :[{}]", res);
		return res;
	}


}
