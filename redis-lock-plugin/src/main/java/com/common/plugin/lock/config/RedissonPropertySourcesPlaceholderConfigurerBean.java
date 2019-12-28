package com.common.plugin.lock.config;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

/**
 * description
 * 自定义YML文件加载
 *
 * @author huangsheng 2019/05/06 1:53 PM
 */
@Deprecated
public class RedissonPropertySourcesPlaceholderConfigurerBean {

	//    @Bean(name = "redissonPropertySourcesPlaceholderConfigurer")
	public PropertySourcesPlaceholderConfigurer yaml() {
		PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
		YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
		yaml.setResources(new ClassPathResource("redisson.yaml"));
		configurer.setProperties(yaml.getObject());
		configurer.setIgnoreUnresolvablePlaceholders(true);
		configurer.setOrder(1);
		return configurer;
	}
}