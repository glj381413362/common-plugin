package com.common.plugin.redis.config;

import com.common.plugin.redis.instance.CacheRedisInstanceFactory;
import com.common.plugin.redis.instance.QueueRedisInstanceFactory;
import com.common.plugin.redis.instance.RecordRedisInstanceFactory;
import com.common.plugin.redis.service.impl.HmallRedisServiceImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import redis.clients.jedis.Jedis;

/**
 * description
 *
 * @author roman 2019/05/20 11:47 PM
 */

@Configuration
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE - 1 )
@ConditionalOnClass(Jedis.class)
@Import({QueueJedisConfig.class,CacheJedisConfig.class,RecordJedisConfig.class,BasePropertySourcesPlaceholderConfigurerBean.class})
public class CustomerRedisAutoConfig implements InitializingBean {

    @Bean
    @ConditionalOnBean(CacheJedisConfig.class)
    public CacheRedisInstanceFactory getCacheRedisInstanceFactory(@Autowired CacheJedisConfig cacheJedisConfig){
        return new CacheRedisInstanceFactory(cacheJedisConfig);
    }

    @Bean
    @ConditionalOnBean(QueueJedisConfig.class)
    public QueueRedisInstanceFactory getQueueJedisInstanceFactory(@Autowired QueueJedisConfig queueJedisConfig){
        return new QueueRedisInstanceFactory(queueJedisConfig);
    }

    @Bean
    @ConditionalOnBean(RecordJedisConfig.class)
    public RecordRedisInstanceFactory getRecordRedisInstanceFactory(@Autowired RecordJedisConfig recordJedisConfig){
        return new RecordRedisInstanceFactory(recordJedisConfig);
    }

    @Bean
    @ConditionalOnBean({RecordRedisInstanceFactory.class,QueueRedisInstanceFactory.class,CacheRedisInstanceFactory.class})
    public HmallRedisServiceImpl getHmallRedisServiceImpl(@Autowired CacheRedisInstanceFactory cacheRedisInstanceFactory
            , @Autowired QueueRedisInstanceFactory queueRedisInstanceFactory, @Autowired RecordRedisInstanceFactory recordRedisInstanceFactory){
        return new HmallRedisServiceImpl(cacheRedisInstanceFactory,queueRedisInstanceFactory,recordRedisInstanceFactory);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
