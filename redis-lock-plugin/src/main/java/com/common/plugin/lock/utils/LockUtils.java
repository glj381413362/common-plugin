package com.common.plugin.lock.utils;

import com.common.plugin.lock.data.RedisLockEntity;
import com.common.plugin.lock.data.RedisLockInstanceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author WangGang
 * @Title EpoLockUtils
 * @Description
 * @date 2018/8/21 19:02
 */
public class LockUtils {

    private static final Logger LOG = LoggerFactory.getLogger(LockUtils.class);
    /**
     * 加锁
     *
     * @param instanceManager redis 锁服务器实例
     * @param key             锁的特异性key
     * @param expireTime      持有锁的时间
     * @param identify        唯一标志
     * @return 加锁结果 为空标识加锁失败
     */
    public static RedisLockEntity tryLock(RedisLockInstanceManager instanceManager, String key, long expireTime, String identify) {
        RedisLockEntity lockEntity = new RedisLockEntity(instanceManager, key);
        if (!lockEntity.tryLock(expireTime, identify)) {
            return null;
        }
        return lockEntity;
    }

    /**
     * 加锁
     *
     * @param instanceManager redis 锁服务实例
     * @param key             锁的特异性key
     * @param waitTime        拿锁的等待时间
     * @param expireTime      持有锁的时间
     * @param identify        唯一标志
     * @return 加锁结果 为空标识加锁失败
     */
    public static RedisLockEntity tryLock(RedisLockInstanceManager instanceManager, String key, long waitTime, long expireTime, String identify) {
        RedisLockEntity lockEntity = new RedisLockEntity(instanceManager, key);
        if (!lockEntity.tryLock(waitTime, expireTime, identify)) {
            return null;
        }
        return lockEntity;
    }

    /**
     * 强制解锁
     * @param instanceManager
     * @param key
     * @param identify
     */
    public static void forceUnLock(RedisLockInstanceManager instanceManager, String key,String identify){
        final RedisLockEntity lockEntity = new RedisLockEntity(instanceManager, key);
        lockEntity.forceUnLock(identify);
    }
}
