package com.common.plugin.lock.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * description
 *
 * @author roman 2019/05/22 11:39 AM
 */
@Configuration
@Data
//@ConditionalOnProperty(prefix = "spring.redisson",name = "enabled",havingValue = "true")
public class RedissonConfig implements InitializingBean {
    private static Logger LOG = LoggerFactory.getLogger(RedissonConfig.class);
    @Value("${spring.redisson.connectTimeout}")
    private int connectTimeout;
    @Value("${spring.redisson.connectionPoolSize}")
    private int connectionPoolSize;
    @Value("${spring.redisson.connectionMinimumIdleSize}")
    private int connectionMinimumIdleSize;
    @Value("${spring.redisson.idleConnectionTimeout}")
    private int idleConnectionTimeout;
    @Value("${spring.redisson.password}")
    private String password;
    @Value("${spring.redisson.lockRedisUrls}")
    private String lockRedisUrls;
    @Value("${spring.redisson.database}")
    private int database;
    @Value("${spring.redisson.standAlone}")
    private Boolean standAlone;

    @Override
    public String toString() {
        return "RedissonConfig{" +
                "connectTimeout=" + connectTimeout +
                ", connectionPoolSize=" + connectionPoolSize +
                ", connectionMinimumIdleSize=" + connectionMinimumIdleSize +
                ", idleConnectionTimeout=" + idleConnectionTimeout +
                ", password='" + password + '\'' +
                ", lockRedisUrls='" + lockRedisUrls + '\'' +
                ", database=" + database +
                '}';
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if(StringUtils.isBlank(password)){
            this.password = null;
        }
        if(LOG.isDebugEnabled()){
            LOG.debug("RedissonConfig info  is : {}",toString());
        }
    }
}
