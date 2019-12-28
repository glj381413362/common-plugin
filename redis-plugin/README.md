# redis-plugin 插件的使用

### 如何使用？
> 使用时只需要将插件的jar的maven坐标引入到当前的项目pom文件中即可，插件会自动装配redis所需对象，需操作redis时注入HmallRedisServer即可


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
  <artifactId>redis-plugin</artifactId>
  <version>0.0.1-RELEASE</version>
</dependency>
```
> Java使用示例
```java
package org.hmall.client.controller;

import com.common.plugin.redis.service.HmallRedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

/**
 * description
 *
 * @author huangsheng 2019/05/22 4:39 PM
 */
@RestController
public class TestRedisController {

    private static final Logger LOG = LoggerFactory.getLogger(TestRedisController.class);

    @Autowired
    private HmallRedisService hmallRedisService;

    /**
     * jedis使用示例
     *
     * @return void
     * @author huangsheng 2019-05-22 5:15 PM
     */
    @GetMapping("/demo/redis")
    public void get() {
        Jedis jedis = null;
        try {
            //===============================================================================
            //  获取对应的redis连接示例 目前提供 cache Record Queue
            //===============================================================================
            jedis = hmallRedisService.getCacheJedis();
            if(jedis == null){
                LOG.error("获取jedis 连接为空");
                return;
            }
            //===============================================================================
            //  业务处理 操作redis
            //===============================================================================
            jedis.set("testKey","test");
        }catch (Exception e){
            LOG.error("获取jedis连接异常：{}",e);
            //TODO:异常处理
        }finally {
            //===============================================================================
            //  注意一定要关闭连接
            //===============================================================================
            if(jedis != null){
                jedis.close();
            }
        }

    }
}

```
### 如需覆盖插件中的对应配置
> 复制插件中redis.yaml文件中内容，当然不需要全量复制，你可以只复制其中的某些内容进行修改，未修改的内容将以插件的中默认配置为准

`注意：由于配置文件优先级问题，此配置要覆盖必须写在application.yml中，否则覆盖不生效`

```yaml
spring:
  cacheRedis:
    database: 0
    # 数据库索引
    host: localhost
    port: 6379
    password:
    jedis:
      pool:
        minIdle: 8
        maxIdle: 32
        maxTotal: 32
        maxWaitMillis: 30000
    #连接超时时间
    timeout: 10000
  recordRedis:
    database: 1
    # 数据库索引
    host: localhost
    port: 6379
    password:
    jedis:
      pool:
        minIdle: 8
        maxIdle: 32
        maxTotal: 32
        maxWaitMillis: 30000
    #连接超时时间
    timeout: 10000
  queueRedis:
    database: 2
    # 数据库索引
    host: localhost
    port: 6379
    password:
    jedis:
      pool:
        minIdle: 8
        maxIdle: 32
        maxTotal: 32
        maxWaitMillis: 30000
    #连接超时时间
    timeout: 10000
```

