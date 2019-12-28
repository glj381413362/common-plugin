package com.common.plugin.sms.service.impl;

import com.alibaba.fastjson.JSON;
import com.common.plugin.sms.data.SmsCheckCodeData;
import com.common.plugin.sms.data.SmsReqData;
import com.common.plugin.sms.constaint.SmsTpye;
import com.common.plugin.sms.exception.SendFailException;
import com.common.plugin.sms.exception.StoreMsgFailException;
import com.common.plugin.sms.service.SmsService;
import com.common.plugin.sms.service.SmsStoreStrategyService;
import com.common.plugin.sms.service.SmsStrategyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * description
 *
 * @author roman 2019/06/05 6:09 PM
 */
public class SmsServiceImpl implements SmsService {
    private static final Logger LOG = LoggerFactory.getLogger(SmsServiceImpl.class);
    private SmsStrategyService smsStrategyService;
    private SmsStoreStrategyService smsStoreStrategyService;
    // 默认120s
    @Value("${sms.checkcode.expireTime:120}")
    private int expireTime;
    /**
     * 验证码类型，营销类以及验证码
     */
    private static final String SMS_TYPE = "CODE";

    private Boolean isStore;

    public Boolean getStore() {
        return isStore;
    }

    public SmsServiceImpl setStore(Boolean store) {
        isStore = store;
        return this;
    }

    public SmsServiceImpl() {
    }

    public SmsServiceImpl(SmsStrategyService smsStrategyService, SmsStoreStrategyService smsStoreStrategyService,Boolean isStore) {
        this.smsStrategyService = smsStrategyService;
        this.smsStoreStrategyService = smsStoreStrategyService;
        this.isStore = isStore;
    }

    @Override
    public void sendSms(SmsReqData smsReqData) throws SendFailException, StoreMsgFailException {
        if(LOG.isDebugEnabled()){
            LOG.debug("1.1 SmsServiceImpl#sendSms param is {}", JSON.toJSONString(smsReqData));
        }
        //===============================================================================
        //  1. 如果是短信验证码，默认存储到redis
        //     redis存储的key为：%s:sms:%s:%s（prefix:sms:验证码类型:手机号码）
        //===============================================================================
        if(SmsTpye.CODE.toString().equalsIgnoreCase(smsReqData.getType())){
            if(LOG.isDebugEnabled()){
                LOG.debug("1.2 is checkCode type ,start store into redis ,param is {}", JSON.toJSONString(smsReqData));
            }
            smsStoreStrategyService.storeCheckCode(
                    new SmsCheckCodeData()
                            .setCheckCode(smsReqData.getCheckCode())
                    .setSmsContentJson(smsReqData.getSmsContentJson())
                    .setCheckCodeType(smsReqData.getCheckCodeType())
                    .setPhoneNumbers(smsReqData.getPhoneNumbers())
                    .setLastSendTime(System.currentTimeMillis())
                    .setExpirationTime(expireTime)
            );
        }

        //===============================================================================
        // 2. 存储完成后进行发送
        //===============================================================================
        smsStrategyService.sendSms(smsReqData);

        //===============================================================================
        // 3. 存储发送历史记录 默认策略不提供存储
        //===============================================================================
        if (isStore){
            //===============================================================================
            //  非验证码类型的短信，使用客制化的存储策略进行存储，默认不进行存储isStore为false可手动开启
            //===============================================================================
            if(LOG.isDebugEnabled()){
                LOG.debug("1.3 is not checkCode type ,use customer storeStrategy to store" +
                        " ,param is {}", JSON.toJSONString(smsReqData));
            }
            smsStoreStrategyService.storeHistory(smsReqData);
        }

    }

    @Override
    public Boolean verifyCode(String code, String checkCodeType ,String phone) {
        SmsCheckCodeData smsCheckCodeData = smsStoreStrategyService.queryCheckCode(new SmsCheckCodeData()
                .setCheckCodeType(checkCodeType)
                .setPhoneNumbers(phone)
        );
        if(smsCheckCodeData == null){
            return false;
        }
        //===============================================================================
        //  取code的值判断是非输入的验证码和发送的一致
        //===============================================================================
        if(code.equals(smsCheckCodeData.getCheckCode())){
            return true;
        }
        return false;
    }
}
