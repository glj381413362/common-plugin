package com.common.plugin.lock.config;

import com.common.plugin.lock.data.RedisLockInstanceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import redis.clients.jedis.Jedis;

/**
 * description
 *
 * @author roman 2019/05/22 11:38 AM
 */
@Configuration
@ConditionalOnClass(Jedis.class)
@Import({RedissonPropertySourcesPlaceholderConfigurerBean.class,RedissonConfig.class})
public class CustomerRedissonAutoConfig {

    @Bean("redisLockInstanceManager")
    @ConditionalOnClass(RedissonConfig.class)
    public RedisLockInstanceManager getRedisLockInstanceManager(@Autowired RedissonConfig redissonConfig){
        return new RedisLockInstanceManager(
                redissonConfig.getConnectTimeout(),
                redissonConfig.getConnectionPoolSize(),
                redissonConfig.getConnectionMinimumIdleSize(),
                redissonConfig.getIdleConnectionTimeout(),
                redissonConfig.getPassword(),
                redissonConfig.getLockRedisUrls(),
                redissonConfig.getDatabase(),
                redissonConfig.getStandAlone()
        );
    }
}
