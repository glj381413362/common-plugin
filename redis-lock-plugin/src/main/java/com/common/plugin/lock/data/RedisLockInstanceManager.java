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

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mark.bao@hand-china.com
 * @date 2018/7/31 17:15
 */
public class RedisLockInstanceManager {
    private final List<RedissonClient> lockRedisClients;
    private Boolean standAlone;

    public Boolean getStandAlone() {
        return standAlone;
    }

    public RedisLockInstanceManager(final int connectTimeout,
                                    final int connectionPoolSize,
                                    final int connectionMinimumIdleSize,
                                    final int idleConnectionTimeout,
                                    final String password,
                                    final String lockRedisUrls,
                                    final int database,
                                    final Boolean standAlone) {
        this.standAlone = standAlone;
        if (StringUtils.isEmpty(lockRedisUrls)) {
            throw new IllegalArgumentException("missing parameter: [redis.lock.cluster.urls]");
        }
        final String[] instanceIpAddress = lockRedisUrls.split(",");
        if(!standAlone){
            if ((instanceIpAddress.length == 1) || (instanceIpAddress.length % 2 == 0)) {
                throw new IllegalArgumentException("current redis lock instance num must be more than one and odd!");
            }
        }

        this.lockRedisClients = new ArrayList<>();
        for (String lockRedisUrl : instanceIpAddress) {
            final Config baseConfig = new Config();
            baseConfig.useSingleServer()
                    .setConnectTimeout(connectTimeout)
                    .setConnectionPoolSize(connectionPoolSize)
                    .setConnectionMinimumIdleSize(connectionMinimumIdleSize)
                    .setIdleConnectionTimeout(idleConnectionTimeout)
                    .setPassword(password)
                    .setAddress(lockRedisUrl)
                    .setDatabase(database);
            lockRedisClients.add(Redisson.create(baseConfig));
        }
    }

    public void destroy() {
        for (RedissonClient redissonClient : lockRedisClients) {
            redissonClient.shutdown();
        }
    }

    public List<RedissonClient> getLockRedisClients() {
        return this.lockRedisClients;
    }
}
