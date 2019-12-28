package com.common.plugin.sms.service.impl;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import com.common.plugin.redis.service.HmallRedisService;
import com.common.plugin.sms.data.SmsCheckCodeData;
import com.common.plugin.sms.exception.StoreMsgFailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

/**
 * description
 *
 * @author roman 2019/06/05 11:25 PM
 */
public class RedisStoreStrategyServiceImpl extends AbstractSmsStoreStrategyService {
    private static final Logger LOG = LoggerFactory.getLogger(RedisStoreStrategyServiceImpl.class);

    private HmallRedisService redisServiceImpl;

    /**
     * redis存储的结构
     */
    private static final String SMS_KEY = "sms:%s:%s";

    public RedisStoreStrategyServiceImpl() {
    }

    public RedisStoreStrategyServiceImpl(HmallRedisService redisServiceImpl) {
        this.redisServiceImpl = redisServiceImpl;
    }

    @Override
    public void storeCheckCode(SmsCheckCodeData smsReqData) {
        //===============================================================================
        //  如果有前缀加上前缀没有不添加
        //===============================================================================
        String prefixStr = StringUtils.isBlank(prefix) ? "" : prefix + ":";
        //===============================================================================
        //  redis存储的key为：%s:sms:%s:%s（前缀:sms:验证码类型:手机号码）
        //===============================================================================
        String key = prefixStr + String.format(SMS_KEY, smsReqData.getCheckCodeType(), smsReqData.getPhoneNumbers());
        Map<String, String> checkCodeData = new HashMap<>();
        checkCodeData.put("checkCode", smsReqData.getCheckCode());
        checkCodeData.put("lastSendTime", String.valueOf(smsReqData.getLastSendTime()));
        checkCodeData.put("expirationTime", getExpireTime(smsReqData.getExpirationTime()));
        Jedis jedis = null;
        try {
            jedis = redisServiceImpl.getRecordJedis();
            jedis.hmset(key, checkCodeData);
            jedis.expire(key, smsReqData.getExpirationTime());
        } catch (Exception e) {
            throw new StoreMsgFailException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public SmsCheckCodeData queryCheckCode(SmsCheckCodeData smsReqData) {
        //===============================================================================
        //  如果有前缀加上前缀没有不添加
        //===============================================================================
        String prefixStr = StringUtils.isBlank(prefix) ? "" : prefix + ":";
        String key = prefixStr + String.format(SMS_KEY, smsReqData.getCheckCodeType(), smsReqData.getPhoneNumbers());
        Jedis jedis = null;
        try{
            jedis = redisServiceImpl.getRecordJedis();
            Map<String, String> msg = redisServiceImpl.getRecordJedis().hgetAll(key);
            SmsCheckCodeData smsCheckCodeData = JSON.parseObject(JSON.toJSONString(msg), SmsCheckCodeData.class);
            return smsCheckCodeData;
        }catch (Exception e){
            LOG.error("验证码获取失败:{}",e);
            return null;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }

    }

    /**
     * 获取验证码过期时间
     * @param expirationTime
     * @return
     */
    protected String getExpireTime(Integer expirationTime){
       return String.valueOf(System.currentTimeMillis() + expirationTime * 1000);
    }
}
