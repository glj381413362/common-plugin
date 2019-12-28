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
public class RecordJedisConfig implements InitializingBean {
    private static Logger LOG = LoggerFactory.getLogger(RecordJedisConfig.class);

    @Value("${spring.recordRedis.jedis.pool.maxTotal}")
    public int redisPoolMaxActive;

    @Value("${spring.recordRedis.jedis.pool.maxWaitMillis}")
    public int redisPoolMaxWait;

    @Value("${spring.recordRedis.jedis.pool.maxIdle}")
    public int redisPoolMaxIdle;

    @Value("${spring.recordRedis.jedis.pool.minIdle}")
    public int redisPoolMinIdle;

    @Value("${spring.recordRedis.password}")
    public String password;

    @Value("${spring.recordRedis.timeout}")
    public int timeout;

    @Value("${spring.recordRedis.database}")
    public int database;

    @Value("${spring.recordRedis.host}")
    public String host;

    @Value("${spring.recordRedis.port}")
    public int port;

    @Override
    public String toString() {
        return "RecordJedisConfig{" +
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
        if(LOG.isDebugEnabled()){
            LOG.debug("RecordJedisConfig info is : {}",toString());
        }
    }
}
