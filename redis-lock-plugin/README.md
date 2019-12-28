# redis-lock-plugin 插件的使用

### 如何使用？
> 使用时只需要将插件的jar的maven坐标引入到当前的项目pom文件中即可，插件会自动装配redssion所需对象

- 添加hmall私服地址
```xml
<repositories>
    <!--hmall 插件私服-->
    <repository>
        <id>hmall-plugins-mixed</id>
        <name>hmall plugins mixed Repository</name>
        <url>http://test.sgp.shou-xuan.com:30107/repository/hmall-plugins-mixed</url>
    </repository>
</repositories>
```

- 添加依赖
```xml
<dependency>
    <groupId>org.hmall</groupId>
    <artifactId>redis-lock-plugin</artifactId>
    <version>0.0.1-RELEASE</version>
</dependency>
```
> Java使用示例
```java
package org.hmall.client.controller;

import com.common.plugin.lock.data.RedisLockEntity;
import com.common.plugin.lock.data.RedisLockInstanceManager;
import com.common.plugin.lock.utils.LockUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * description
 *
 * @author huangsheng 2019/05/22 4:39 PM
 */
@RestController
public class TestController {

    private static final Logger LOG = LoggerFactory.getLogger(TestController.class);

    @Autowired
    RedisLockInstanceManager redisLockInstanceManager;

    /**
     * 获取锁示例
     *
     * @return void
     * @author huangsheng 2019-05-22 5:15 PM
     */
    @GetMapping("/getLock")
    public void get() {
        //===============================================================================
        //  获取锁
        //  tryLock参数说明如下：
        //  @param instanceManager redis 锁服务实例
        //  @param key             需要锁的对象key
        //  @param waitTime        拿锁的等待时间
        //  @param expireTime      持有锁的时间
        //  @param identify        操作唯一标志 
        //===============================================================================
        RedisLockEntity lockEntity = LockUtils.tryLock(redisLockInstanceManager, "testlockKey", 5000, 200000, "identify");
        if (lockEntity == null) {
            LOG.error("获取锁失败");
            return;
        }
        
        //===============================================================================
        //  下面进行业务代码书写
        //===============================================================================
        try {
            //TODO：业务代码
        } catch (Exception e) {
            //TODO:异常处理
        } finally {
            //如需释放锁请在finally中释放
            lockEntity.unLock("identify");
        }

    }
    
    /**
     *
     *  强制释放锁
     *
     * @author huangsheng 2019-05-22 5:22 PM
     * @return void
     */
    @GetMapping("/releaseLock")
    public void release() {
        
        LockUtils.forceUnLock(redisLockInstanceManager, "testlockKey", "identify");
    }
}

```
### 如需覆盖插件中的对应配置
> 复制插件中redisson.yaml文件中内容，当然不需要全量复制，你可以只复制其中的某些内容进行修改，未修改的内容将以插件的中默认配置为准

`注意：由于配置文件优先级问题，此配置要覆盖必须写在application.yml中，否则覆盖不生效`

- 重要配置参数说明
```
spring.redisson.standAlone 
为true 代表需要配置的所有redis实例加锁成功才成功 
为false代表只要半数以上的redis示例加锁成功就成功

推荐配置：
当只有一个示例时建议配置为true
当有多个并且为奇数个实例时配置为false
生产环境建议使用多实例配置保证锁服务的高可用

```
```yaml
spring:
  redisson:
    connectTimeout: 3000 #连接超时时间
    connectionMinimumIdleSize: 8 #连接池最小连接数
    connectionPoolSize: 32 #连接池大小
    idleConnectionTimeout: 30000 #空闲连接释放时间
    password: #密码
    lockRedisUrls: localhost:6379 #连接URL 多个使用","分隔
    database: 1 #使用的库
    standAlone: true #默认为true
```

