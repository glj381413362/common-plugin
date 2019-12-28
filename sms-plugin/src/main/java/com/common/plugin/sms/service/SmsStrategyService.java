package com.common.plugin.sms.service;

import com.common.plugin.sms.data.SmsReqData;
import com.common.plugin.sms.exception.SendFailException;
/**
 * description
 *
 * @author roman 2019/06/05 11:04 PM
 */
public interface SmsStrategyService {
    /**
     * 短信发送
     */
    void sendSms(SmsReqData smsReqData) throws SendFailException;

    /**
     * 必要参数校验
     */
    void checkParam(SmsReqData smsReqData);
}
