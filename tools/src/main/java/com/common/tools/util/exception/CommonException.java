//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.common.tools.util.exception;

import com.common.tools.util.pojo.Msg;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CommonException extends RuntimeException {
	private final transient Object[] parameters;
	private String code;

	/**
	 * new CommonException(new Msg("id:{},error:{}"),id,error)
	 */
	public CommonException(Msg msg, Object... params) {
		super(msg.getMsg(params));
		this.parameters = new Object[0];
		this.code = msg.getMsg(params);
	}

	/**
	 * new CommonException(cause,new Msg("id:{},error:{}"),id,error)
	 */
	public CommonException(Throwable cause, Msg msg, Object... params) {
		super(msg.getMsg(params), cause);
		this.parameters = new Object[0];
		this.code = msg.getMsg(params);
	}

	public CommonException(String code, Object... parameters) {
		super(code);
		this.parameters = parameters;
		this.code = code;
	}

	public CommonException(String code, Throwable cause, Object... parameters) {
		super(code, cause);
		this.parameters = parameters;
		this.code = code;
	}

	public CommonException(String code, Throwable cause) {
		super(code, cause);
		this.code = code;
		this.parameters = new Object[0];
	}


	public CommonException(Throwable cause, Object... parameters) {
		super(cause);
		this.parameters = parameters;
	}

	public Object[] getParameters() {
		return this.parameters;
	}

	public String getCode() {
		return this.code;
	}

	public String getTrace() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		this.printStackTrace(ps);
		ps.flush();
		return new String(baos.toByteArray());
	}

	public Map<String, Object> toMap() {
		HashMap<String, Object> map = new LinkedHashMap();
		map.put("code", this.code);
		map.put("message", super.getMessage());
		return map;
	}
}
