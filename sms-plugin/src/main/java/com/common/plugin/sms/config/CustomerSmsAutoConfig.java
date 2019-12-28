package com.common.plugin.sms.config;

import com.common.plugin.redis.config.CustomerRedisAutoConfig;
import com.common.plugin.redis.service.HmallRedisService;
import com.common.plugin.sms.service.SmsService;
import com.common.plugin.sms.service.SmsStoreStrategyService;
import com.common.plugin.sms.service.SmsStrategyService;
import com.common.plugin.sms.service.impl.AliSmsStrategyServiceImpl;
import com.common.plugin.sms.service.impl.BaiduSmsStrategyServiceImpl;
import com.common.plugin.sms.service.impl.RedisStoreStrategyServiceImpl;
import com.common.plugin.sms.service.impl.SmsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * description
 *
 * @author roman 2019/06/05 9:58 PM
 */

@Configuration
@AutoConfigureAfter(CustomerRedisAutoConfig.class)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE )
@Import(value = {AliSmsConfig.class})
public class CustomerSmsAutoConfig {

    private SmsStoreStrategyService smsStoreStrategyService;

    /**
     * 缓存所有的短信策略
     */
    public static Map<String,Class<? extends SmsStrategyService>> smsSendStrategy = new HashMap(){{
        put("ALI",AliSmsStrategyServiceImpl.class);
        put("BAIDU",BaiduSmsStrategyServiceImpl.class);
    }};

    @Value("${sms.send.strategy:ALI}")
    private String strategy;

    @Value("${sms.isStore:false}")
    private Boolean isStore;

    /**
     * 只要配置了 `sms.send.strategy:ALI` 那么默认的就是使用阿里发送短信
     * @param aliSmsConfig
     * @return
     */
    @Bean
    public SmsStrategyService getSmsStrategyDefaultAliService(@Autowired AliSmsConfig aliSmsConfig) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        //===============================================================================
        //  客制化短信策略集合
        //===============================================================================
        ServiceLoader<SmsStrategyService> smsStrategyLoader = ServiceLoader.load(SmsStrategyService.class);

        //===============================================================================
        //  没有客制化的直接使用默认的
        //===============================================================================
        SmsStrategyService smsStrategyService;
        if(!smsStrategyLoader.iterator().hasNext()){
            Class<? extends SmsStrategyService> smsClass =  smsSendStrategy.get(strategy);
            Constructor<? extends SmsStrategyService> constructor = smsClass.getConstructor(AliSmsConfig.class);
            smsStrategyService =constructor.newInstance(aliSmsConfig);
        }else {
//            smsStrategyLoader.iterator().forEachRemaining(it ->{
//                System.out.println(it.getClass());
//            });

            //===============================================================================
            //  如果实现有多个只获取第一个
            //===============================================================================
            smsStrategyService = smsStrategyLoader.iterator().next();
        }
        return smsStrategyService;
    }

    @Bean
    public SmsStoreStrategyService getSmsStoreService( @Autowired HmallRedisService redisServiceImpl){
        //===============================================================================
        //  短信存储策略集合
        //===============================================================================
        ServiceLoader<SmsStoreStrategyService> smsStrategyLoader = ServiceLoader.load(SmsStoreStrategyService.class);

        if(!smsStrategyLoader.iterator().hasNext()){
            smsStoreStrategyService = new RedisStoreStrategyServiceImpl(redisServiceImpl);
        }else {
            //===============================================================================
            //  如果实现有多个只获取第一个
            //===============================================================================
            SmsStoreStrategyService smsStrategyService = smsStrategyLoader.iterator().next();
            smsStoreStrategyService = smsStrategyService;
        }
        return smsStoreStrategyService;
    }

    @Bean
    public SmsService getSmsService(@Autowired SmsStrategyService smsStrategyService,@Autowired SmsStoreStrategyService smsStoreStrategyService){
        return new SmsServiceImpl(smsStrategyService,smsStoreStrategyService,isStore);
    }
}
