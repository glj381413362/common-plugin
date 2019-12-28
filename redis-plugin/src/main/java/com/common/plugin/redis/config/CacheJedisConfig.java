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
public class CacheJedisConfig implements InitializingBean {

    private Logger LOG = LoggerFactory.getLogger(CacheJedisConfig.class);

    @Value("${spring.cacheRedis.jedis.pool.maxTotal}")
    public int redisPoolMaxActive;

    @Value("${spring.cacheRedis.jedis.pool.maxWaitMillis}")
    public int redisPoolMaxWait;

    @Value("${spring.cacheRedis.jedis.pool.maxIdle}")
    public int redisPoolMaxIdle;

    @Value("${spring.cacheRedis.jedis.pool.minIdle}")
    public int redisPoolMinIdle;

    @Value("${spring.cacheRedis.password}")
    public String password;

    @Value("${spring.cacheRedis.timeout}")
    public int timeout;

    @Value("${spring.cacheRedis.database}")
    public int database;

    @Value("${spring.cacheRedis.host}")
    public String host;

    @Value("${spring.cacheRedis.port}")
    public int port;

    @Override
    public String toString() {
        return "CacheJedisConfig{" +
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
        if (LOG.isDebugEnabled()){
            LOG.debug("CacheJedisConfig info : {}",toString());
        }
    }
}
