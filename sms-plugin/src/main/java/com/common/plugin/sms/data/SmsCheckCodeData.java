package com.common.plugin.sms.data;

import java.util.Map;

/**
 * description
 *
 * @author roman 2019/06/12 3:21 PM
 */
public class SmsCheckCodeData {
    /**
     * 验证码类型，方便redis存储的key赋值
     */
    private String checkCodeType;
    private String templateCode;
    private String phoneNumbers;
    private Long lastSendTime;
    private int expirationTime ;
    /**
     * 验证码
     */
    private String checkCode;
    private Map<String,String> smsContentJson;

    public String getCheckCodeType() {
        return checkCodeType;
    }

    public SmsCheckCodeData setCheckCodeType(String checkCodeType) {
        this.checkCodeType = checkCodeType;
        return this;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public SmsCheckCodeData setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
        return this;
    }

    public String getPhoneNumbers() {
        return phoneNumbers;
    }

    public SmsCheckCodeData setPhoneNumbers(String phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
        return this;
    }

    public Long getLastSendTime() {
        return lastSendTime;
    }

    public SmsCheckCodeData setLastSendTime(Long lastSendTime) {
        this.lastSendTime = lastSendTime;
        return this;
    }

    public int getExpirationTime() {
        return expirationTime;
    }

    public SmsCheckCodeData setExpirationTime(int expirationTime) {
        this.expirationTime = expirationTime;
        return this;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public SmsCheckCodeData setCheckCode(String checkCode) {
        this.checkCode = checkCode;
        return this;
    }

    public Map<String, String> getSmsContentJson() {
        return smsContentJson;
    }

    public SmsCheckCodeData setSmsContentJson(Map<String, String> smsContentJson) {
        this.smsContentJson = smsContentJson;
        return this;
    }
}
