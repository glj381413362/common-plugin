package com.common.plugin.sms.service;

import com.common.plugin.sms.data.SmsCheckCodeData;
import com.common.plugin.sms.data.SmsReqData;

/**
 * description
 *
 * @author roman 2019/06/05 11:04 PM
 */
public interface SmsStoreStrategyService {
    /**
     * 短信存储
     * @return
     */
    void storeHistory(SmsReqData smsReqData);

    /**
     * 验证码短信存储
     * @return
     */
    void storeCheckCode(SmsCheckCodeData smsReqData);

    /**
     * 验证码短信查询
     * @return
     */
    SmsCheckCodeData queryCheckCode(SmsCheckCodeData smsReqData);
}
