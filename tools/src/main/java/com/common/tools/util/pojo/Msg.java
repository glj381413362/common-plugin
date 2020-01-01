package com.common.tools.util.pojo;

import com.common.tools.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

/**
 * <p>
 *
 * </p>
 *
 * @author gongliangjun 2019/07/01 11:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Msg {
	/*
	模板信息 id:{},name:{}
	 */
	private String msgTemplate;


	public String getMsg(Object... params){
		return StringUtil.strFormat(this.msgTemplate, params);
	}
}
