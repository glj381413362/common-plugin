package com.common.plugin.redis.instance;

import com.common.plugin.redis.config.QueueJedisConfig;
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
public class QueueRedisInstanceFactory implements DisposableBean {

    private final JedisPool queueRedisPool;

    private QueueJedisConfig queueJedisConfig;

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

    public QueueRedisInstanceFactory(QueueJedisConfig queueJedisConfig) {
       JedisPoolConfig redisPoolConfig = setPoolConfig(queueJedisConfig.redisPoolMaxIdle, queueJedisConfig.redisPoolMinIdle, queueJedisConfig.redisPoolMaxActive, queueJedisConfig.redisPoolMaxWait, true);
       if(StringUtils.isBlank(queueJedisConfig.password)){
           queueJedisConfig.password = null;
       }
       this.queueJedisConfig = queueJedisConfig;
       this.queueRedisPool = new JedisPool(redisPoolConfig,queueJedisConfig.host,queueJedisConfig.port,queueJedisConfig.timeout,queueJedisConfig.password,queueJedisConfig.database);
    }

    @Override
    public void destroy() throws Exception {
        this.queueRedisPool.destroy();
    }

    public Jedis getResource() {
        return this.queueRedisPool.getResource();
    }

}
