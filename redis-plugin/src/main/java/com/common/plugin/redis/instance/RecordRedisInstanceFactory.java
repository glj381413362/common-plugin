package com.common.plugin.redis.instance;


import com.common.plugin.redis.config.RecordJedisConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * description
 *
 * @author huangsheng 2019/04/25 11:34 AM
 */

public class RecordRedisInstanceFactory implements DisposableBean {

    private final JedisPool recordRedisPool;

    private RecordJedisConfig recordJedisConfig;

    /**
     * 设置连接池属性
     *
     * @param maxIdle
     * @param minIdle
     * @param maxActive
     * @param maxWait
     * @param testOnBorrow
     * @return
     */
    public JedisPoolConfig setPoolConfig(int maxIdle, int minIdle, int maxActive, int maxWait, boolean testOnBorrow) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxTotal(maxActive);
        poolConfig.setMaxWaitMillis(maxWait);
        poolConfig.setTestOnBorrow(testOnBorrow);
        return poolConfig;
    }

    public RecordRedisInstanceFactory(RecordJedisConfig recordJedisConfig) {
       JedisPoolConfig redisPoolConfig = setPoolConfig(recordJedisConfig.redisPoolMaxIdle, recordJedisConfig.redisPoolMinIdle, recordJedisConfig.redisPoolMaxActive, recordJedisConfig.redisPoolMaxWait, true);
       if(StringUtils.isBlank(recordJedisConfig.password)){
           recordJedisConfig.password = null;
       }
       this.recordJedisConfig = recordJedisConfig;
       this.recordRedisPool = new JedisPool(redisPoolConfig,recordJedisConfig.host,recordJedisConfig.port,recordJedisConfig.timeout,recordJedisConfig.password,recordJedisConfig.database);
    }

    @Override
    public void destroy() throws Exception {
        this.recordRedisPool.destroy();
    }

    public Jedis getResource() {
        return this.recordRedisPool.getResource();
    }

}
