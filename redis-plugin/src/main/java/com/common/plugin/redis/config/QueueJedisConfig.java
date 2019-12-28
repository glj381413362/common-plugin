package com.common.plugin.redis.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * description
 *
 * @author huangsheng 2019/04/25 3:38 PM
 */
@Configuration
public class QueueJedisConfig implements InitializingBean {
    private Logger LOG = LoggerFactory.getLogger(QueueJedisConfig.class);

    @Value("${spring.queueRedis.jedis.pool.maxTotal}")
    public int redisPoolMaxActive;

    @Value("${spring.queueRedis.jedis.pool.maxWaitMillis}")
    public int redisPoolMaxWait;

    @Value("${spring.queueRedis.jedis.pool.maxIdle}")
    public int redisPoolMaxIdle;

    @Value("${spring.queueRedis.jedis.pool.minIdle}")
    public int redisPoolMinIdle;

    @Value("${spring.queueRedis.password}")
    public String password;

    @Value("${spring.queueRedis.timeout}")
    public int timeout;

    @Value("${spring.queueRedis.database}")
    public int database;

    @Value("${spring.queueRedis.host}")
    public String host;

    @Value("${spring.queueRedis.port}")
    public int port;

    @Override
    public String toString() {
        return "QueueJedisConfig{" +
                "redisPoolMaxActive=" + redisPoolMaxActive +
                ", redisPoolMaxWait=" + redisPoolMaxWait +
                ", redisPoolMaxIdle=" + redisPoolMaxIdle +
                ", redisPoolMinIdle=" + redisPoolMinIdle +
                ", password='" + password + '\'' +
                ", timeout=" + timeout +
                ", database=" + database +
                ", host='" + host + '\'' +
                ", port=" + port +
                '}';
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //===============================================================================
        //  加载完属性后打印信息
        //===============================================================================
        if(LOG.isDebugEnabled()) {
            LOG.debug("QueueJedisConfig info : {}", toString());
        }
    }
}
