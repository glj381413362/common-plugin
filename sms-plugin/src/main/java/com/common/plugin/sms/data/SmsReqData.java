package com.common.plugin.sms.data;

import java.io.Serializable;
import java.util.Map;

/**
 * description
 *
 * @author roman 2019/06/06 3:01 PM
 */

public class SmsReqData implements Serializable {
    private String signName;
    private String templateCode;
    private String phoneNumbers;
    /**
     * 短信类型
     */
    private String type;
    /**
     * 验证码
     */
    private String checkCode;
    /**
     * 验证码类型，方便redis存储的key赋值
     */
    private String checkCodeType;
    private Map<String,String> smsContentJson;

    public String getCheckCodeType() {
        return checkCodeType;
    }

    public SmsReqData setCheckCodeType(String checkCodeType) {
        this.checkCodeType = checkCodeType;
        return this;
    }

    public String getType() {
        return type;
    }

    public SmsReqData setType(String type) {
        this.type = type;
        return this;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public SmsReqData setCheckCode(String checkCode) {
        this.checkCode = checkCode;
        return this;
    }

    public String getSignName() {
        return signName;
    }

    public SmsReqData setSignName(String signName) {
        this.signName = signName;
        return this;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public SmsReqData setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
        return this;
    }

    public String getPhoneNumbers() {
        return phoneNumbers;
    }

    public SmsReqData setPhoneNumbers(String phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
        return this;
    }

    public Map<String, String> getSmsContentJson() {
        return smsContentJson;
    }

    public SmsReqData setSmsContentJson(Map<String, String> smsContentJson) {
        this.smsContentJson = smsContentJson;
        return this;
    }
}
