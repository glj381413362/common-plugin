package com.common.plugin.sms.service.impl;

import com.common.plugin.sms.data.SmsReqData;
import com.common.plugin.sms.service.SmsStoreStrategyService;
import org.springframework.beans.factory.annotation.Value;

/**
 * description
 *
 * @author roman 2019/06/12 1:58 PM
 */
public abstract class AbstractSmsStoreStrategyService implements SmsStoreStrategyService {

    @Value("${sms.checkcode.prefix}")
    public String prefix;

    @Override
    public void storeHistory(SmsReqData smsReqData) {

    }
}
