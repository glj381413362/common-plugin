package com.common.plugin.sms.service;

import com.common.plugin.sms.data.SmsReqData;
import com.common.plugin.sms.exception.SendFailException;
import com.common.plugin.sms.exception.StoreMsgFailException;

/**
 * description
 *
 * @author roman 2019/06/05 6:08 PM
 */
public interface SmsService {
    /**
     * 短信发送
     * @param smsReqData
     */
    void sendSms(SmsReqData smsReqData) throws SendFailException, StoreMsgFailException;

    /**
     * 如果短信为验证码提供验证码校验功能
     * @param code
     * @param phone
     * @param checkCodeType:验证码短信的类型
     */
    Boolean verifyCode(String code, String checkCodeType, String phone);
}
