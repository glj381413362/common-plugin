package com.common.plugin.sms.service.impl;

import org.apache.commons.lang3.StringUtils;
import com.common.plugin.sms.data.SmsReqData;
import com.common.plugin.sms.exception.ParamException;
import com.common.plugin.sms.service.SmsStrategyService;

/**
 * description
 *
 * @author roman 2019/06/12 1:58 PM
 */
public abstract class AbstractSmsStrategyService implements SmsStrategyService {

    @Override
    public void checkParam(SmsReqData smsReqData) {
        StringBuilder message = new StringBuilder();
        if (StringUtils.isBlank(smsReqData.getSignName())){
            message.append(String.format("{%s}","signName"));
        }
        if (StringUtils.isBlank(smsReqData.getTemplateCode())){
            message.append(String.format("{%s}","templateCode"));
        }
        if (StringUtils.isBlank(smsReqData.getPhoneNumbers())){
            message.append(String.format("{%s}","phoneNumbers"));
        }
        if(StringUtils.isNotBlank(message.toString())){
            throw new ParamException(message.append("不能为空").toString());
        }
    }
}
