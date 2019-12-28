package com.common.plugin.redis.service;

import redis.clients.jedis.Jedis;

/**
 * description
 * 获取Redis连接
 * @author huangsheng 2019/04/25 10:24 AM
 */
public interface HmallRedisService {

    /**
     * 缓存
     * @return
     */
    Jedis getCacheJedis();

    /**
     * 记录
     * @return
     */
    Jedis getRecordJedis();

    /**
     * 队列
     * @return
     */
    Jedis getQueueJedis();
}
