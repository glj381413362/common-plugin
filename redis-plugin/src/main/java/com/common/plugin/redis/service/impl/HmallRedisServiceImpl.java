package com.common.plugin.redis.service.impl;


import com.common.plugin.redis.instance.CacheRedisInstanceFactory;
import com.common.plugin.redis.instance.QueueRedisInstanceFactory;
import com.common.plugin.redis.instance.RecordRedisInstanceFactory;
import com.common.plugin.redis.service.HmallRedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * description
 *
 * @author huangsheng 2019/04/25 10:25 AM
 */
public class HmallRedisServiceImpl implements HmallRedisService {
    private static final Logger LOG = LoggerFactory.getLogger(HmallRedisServiceImpl.class);

    private CacheRedisInstanceFactory cacheRedisInstanceFactory;

    private QueueRedisInstanceFactory queueRedisInstanceFactory;

    private RecordRedisInstanceFactory recordRedisInstanceFactory;

    public HmallRedisServiceImpl(CacheRedisInstanceFactory cacheRedisInstanceFactory
            ,QueueRedisInstanceFactory queueRedisInstanceFactory,RecordRedisInstanceFactory recordRedisInstanceFactory){
        this.cacheRedisInstanceFactory = cacheRedisInstanceFactory;
        this.queueRedisInstanceFactory = queueRedisInstanceFactory;
        this.recordRedisInstanceFactory = recordRedisInstanceFactory;
    }


    /**
     * 缓存
     *
     * @return
     */
    @Override
    public Jedis getCacheJedis() {
        Jedis cacheJedis = null;
        try {
            cacheJedis = cacheRedisInstanceFactory.getResource();
        } catch (final Exception e) {
            LOG.error("Redis Connection Error：Cannot Connect Cache Redis！Error Message：{}" , e);
        }
        return cacheJedis;
    }

    /**
     * 记录
     *
     * @return
     */
    @Override
    public Jedis getRecordJedis() {
        Jedis recordJedis = null;
        try {
            recordJedis = recordRedisInstanceFactory.getResource();
        } catch (final Exception e) {
            LOG.error("Redis Connection Error：Cannot Connect Record Redis！Error Message：{}" , e);
        }
        return recordJedis;
    }

    /**
     * 队列
     *
     * @return
     */
    @Override
    public Jedis getQueueJedis() {
        Jedis queueJedis = null;
        try {
            queueJedis = queueRedisInstanceFactory.getResource();
        } catch (final Exception e) {
            LOG.error("Redis Connection Error：Cannot Connect Queue Redis！Error Message：{}" , e);
        }
        return queueJedis;
    }
}
