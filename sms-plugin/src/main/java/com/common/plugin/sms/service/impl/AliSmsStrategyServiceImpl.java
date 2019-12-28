package com.common.plugin.sms.service.impl;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.common.plugin.sms.data.SmsReqData;
import com.common.plugin.sms.config.AliSmsConfig;
import com.common.plugin.sms.exception.SendFailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * description
 *
 * @author roman 2019/06/05 11:16 PM
 */
public class AliSmsStrategyServiceImpl extends AbstractSmsStrategyService implements InitializingBean {
    private static Logger LOG = LoggerFactory.getLogger(AliSmsStrategyServiceImpl.class);
    private AliSmsConfig aliSmsConfig;


    private static final String RES_OK = "OK";

    /**
     * 短信发送客户端
     */
    private IAcsClient acsClient;

    public AliSmsConfig getAliSmsConfig() {
        return aliSmsConfig;
    }

    public AliSmsStrategyServiceImpl setAliSmsConfig(AliSmsConfig aliSmsConfig) {
        this.aliSmsConfig = aliSmsConfig;
        return this;
    }

    public AliSmsStrategyServiceImpl() {
    }

    public AliSmsStrategyServiceImpl(AliSmsConfig aliSmsConfig) {
        this.aliSmsConfig = aliSmsConfig;
    }

    @Override
    public void sendSms(SmsReqData smsReqData) throws SendFailException {
        if(LOG.isDebugEnabled()){
            LOG.debug("1.1 AliSmsStrategyServiceImpl#sendSms  param :{}",JSON.toJSONString(smsReqData));
        }

        String signName = smsReqData.getSignName();
        String templateCode = smsReqData.getTemplateCode();
        String phoneNumber = smsReqData.getPhoneNumbers();
        //===============================================================================
        //  校验必要的参数是否为null
        //===============================================================================
        checkParam(smsReqData);
        //组装请求对象
        SendSmsRequest request = new SendSmsRequest();
        //使用post提交
        request.setMethod(MethodType.POST);
        //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式；发送国际/港澳台消息时，接收号码格式为00+国际区号+号码，如“0085200000000”
        request.setPhoneNumbers(phoneNumber);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(signName);
        //必填:短信模板-可在短信控制台中找到，发送国际/港澳台消息时，请使用国际/港澳台短信模版
        request.setTemplateCode(templateCode);
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
        request.setTemplateParam(JSON.toJSONString(smsReqData.getSmsContentJson()));
        //可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");
        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        //request.setOutId("yourOutId");
        //请求失败这里会抛ClientException异常
        SendSmsResponse sendSmsResponse = null;
        try {
            sendSmsResponse = acsClient.getAcsResponse(request);
        } catch (Exception e) {
            throw new SendFailException("message send fail,%s",e.getMessage());
        }

        if(sendSmsResponse != null){
            if(!RES_OK.equals(sendSmsResponse.getCode())){
                throw new SendFailException("message send fail,%s",sendSmsResponse.getMessage());
            }
        }else {
            throw new SendFailException("message send fail");
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //===============================================================================
        //  属性加载完毕后初始化短信发送客户端
        //===============================================================================
        String defaultConnectTimeout = aliSmsConfig.getDefaultConnectTimeout();
        String DefaultReadTimeout = aliSmsConfig.getDefaultReadTimeout();
        String AccessKeyId = aliSmsConfig.getAccessKeyId();
        String AccessKeySecret = aliSmsConfig.getAccessKeySecret();
        String product = aliSmsConfig.getProduct();
        String domain = aliSmsConfig.getDomain();
        //设置超时时间-可自行调整
        System.setProperty("sun.net.client.defaultConnectTimeout", defaultConnectTimeout);
        System.setProperty("sun.net.client.defaultReadTimeout", DefaultReadTimeout);
        //初始化ascClient,暂时不支持多region（请勿修改）
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", AccessKeyId,
                AccessKeySecret);

        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        acsClient = new DefaultAcsClient(profile);
    }
}


