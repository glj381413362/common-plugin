package com.common.plugin.sms.service.impl;

import com.common.plugin.sms.data.SmsReqData;
import com.common.plugin.sms.config.AliSmsConfig;
import com.common.plugin.sms.service.SmsStrategyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * description
 *
 * @author roman 2019/06/05 11:16 PM
 */
public class BaiduSmsStrategyServiceImpl implements SmsStrategyService {
    private static Logger LOG = LoggerFactory.getLogger(BaiduSmsStrategyServiceImpl.class);
    private AliSmsConfig aliSmsConfig;

    public AliSmsConfig getAliSmsConfig() {
        return aliSmsConfig;
    }

    public BaiduSmsStrategyServiceImpl setAliSmsConfig(AliSmsConfig aliSmsConfig) {
        this.aliSmsConfig = aliSmsConfig;
        return this;
    }

    public BaiduSmsStrategyServiceImpl() {
    }

    public BaiduSmsStrategyServiceImpl(AliSmsConfig aliSmsConfig) {
        this.aliSmsConfig = aliSmsConfig;
    }

    @Override
    public void sendSms(SmsReqData smsReqData) {

    }

    @Override
    public void checkParam(SmsReqData smsReqData) {

    }
}
