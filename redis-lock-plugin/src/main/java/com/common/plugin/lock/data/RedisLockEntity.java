/*
 *
 * Copyright (C) HAND Enterprise Solutions Company Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.common.plugin.lock.data;

import org.redisson.RedissonMultiLock;
import org.redisson.RedissonRedLock;
import org.redisson.api.RFuture;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author mark.bao@hand-china.com
 * @date 2018/7/31 17:14
 */
public class RedisLockEntity {
    private static final Logger LOG = LoggerFactory.getLogger(RedisLockEntity.class);

    private final String lockKey;
    private final String lockUid;
    private final RLock[] redisLocks;
    private final RedissonMultiLock redissonRedLock;

    public RedisLockEntity(final RedisLockInstanceManager epoRedisLockInstanceManager,
                           final String lockKey) {
        this.lockKey = lockKey;
        this.lockUid = "T" + Thread.currentThread().getId();

        final List<RedissonClient> redisLockClients = epoRedisLockInstanceManager.getLockRedisClients();
        final int length = redisLockClients.size();
        this.redisLocks = new RLock[length];
        for (int i = 0; i < length; i++) {
            final RLock rLock = redisLockClients.get(i).getLock(lockKey);
            this.redisLocks[i] = rLock;
        }
        if(epoRedisLockInstanceManager.getStandAlone()){
            this.redissonRedLock = new RedissonMultiLock(this.redisLocks);
        }else {
            this.redissonRedLock = new RedissonRedLock(this.redisLocks);
        }
    }

    public boolean tryLock(final long expireTime, final String identify) {
        final boolean lockSuccess = this.tryLockInternal(0, expireTime);
        if (lockSuccess) {
            LOG.info(identify + " Redis Lock[" + lockKey + ", " + lockUid + "] try Lock no wait successful ... expireTime[" + expireTime + "]ms!");
            return true;
        } else {
            LOG.info(identify + " Redis Lock[" + lockKey + ", " + lockUid + "] try Lock no wait failed!");
            return false;
        }
    }

    public boolean tryLock(final long waitTime, final long expireTime, final String identify) {
        final boolean lockSuccess = this.tryLockInternal(waitTime, expireTime);
        if (lockSuccess) {
            LOG.info(identify + " Redis Lock[" + lockKey + ", " + lockUid + "] try Lock wait[" + waitTime + "] successful ... expireTime[" + expireTime + "]ms!");
            return true;
        } else {
            LOG.info(identify + " Redis Lock[" + lockKey + ", " + lockUid + "] try Lock wait[" + waitTime + "] failed!");
            return false;
        }
    }

    private boolean tryLockInternal(final long waitTime, final long expireTime) {
        try {
            return this.redissonRedLock.tryLock(waitTime, expireTime, TimeUnit.MILLISECONDS);
        } catch (final Throwable e) {
            LOG.error("Redis Try Lock[" + lockKey + ", " + lockUid + "] happened error!", e);
            return false;
        }
    }

    public void unLock(final String identify) {
        if (this.unLockInternal()) {
            LOG.info(identify + " Redis Lock[" + lockKey + ", " + lockUid + "] release Lock successful!");
        } else {
            LOG.info(identify + " Redis Lock[" + lockKey + ", " + lockUid + "] release Lock failed!");
        }
    }

    private boolean unLockInternal() {
        final List<RFuture<Void>> unLockResults = new ArrayList<>(this.redisLocks.length);
        for (RLock rLock : this.redisLocks) {
            unLockResults.add(rLock.unlockAsync());
        }

        int successUnlock = 0;
        int failedUnlock = 0;
        for (RFuture<Void> unLockResult : unLockResults) {
            try {
                unLockResult.awaitUninterruptibly();
                successUnlock++;
            } catch (final Throwable e) {
                LOG.error("Redis Release Lock[" + lockKey + ", " + lockUid + "] happened error!", e);
                failedUnlock++;
            }
        }

        return successUnlock > failedUnlock;
    }

    public void forceUnLock(final String identify) {
        List<RFuture<Boolean>> unLockResults = new ArrayList<>(this.redisLocks.length);
        for (RLock rLock : this.redisLocks) {
            unLockResults.add(rLock.forceUnlockAsync());
        }

        int successUnlock = 0;
        int failedUnlock = 0;
        for (RFuture<Boolean> unLockResult : unLockResults) {
            try {
                unLockResult.awaitUninterruptibly();
                successUnlock++;
            } catch (final Throwable e) {
                LOG.error(identify + "Redis Force Release Lock[" + lockKey + ", " + lockUid + "] happened error!", e);
                failedUnlock++;
            }
        }

        if (successUnlock > failedUnlock) {
            LOG.info(identify + " Redis Lock[" + lockKey + ", " + lockUid + "] force release Lock successful!");
        } else {
            LOG.info(identify + " Redis Lock[" + lockKey + ", " + lockUid + "] force release Lock failed!");
        }
    }
}
