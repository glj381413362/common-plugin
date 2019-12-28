package com.common.plugin.redis.instance;


import com.common.plugin.redis.config.CacheJedisConfig;
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
public class CacheRedisInstanceFactory implements DisposableBean {

    private final JedisPool cacheRedisPool;

    private CacheJedisConfig cacheJedisConfig;

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

    public CacheRedisInstanceFactory(CacheJedisConfig cacheJedisConfig) {
       JedisPoolConfig redisPoolConfig = setPoolConfig(cacheJedisConfig.redisPoolMaxIdle, cacheJedisConfig.redisPoolMinIdle, cacheJedisConfig.redisPoolMaxActive, cacheJedisConfig.redisPoolMaxWait, true);
       if(StringUtils.isBlank(cacheJedisConfig.password)){
           cacheJedisConfig.password = null;
       }
       this.cacheJedisConfig = cacheJedisConfig;
       this.cacheRedisPool = new JedisPool(redisPoolConfig,cacheJedisConfig.host,cacheJedisConfig.port,cacheJedisConfig.timeout,cacheJedisConfig.password,cacheJedisConfig.database);
    }

    @Override
    public void destroy() throws Exception {
        this.cacheRedisPool.destroy();
    }

    public Jedis getResource() {
        return this.cacheRedisPool.getResource();
    }

}
