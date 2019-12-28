package com.common.plugin.redis.config;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

/**
 * description
 * 自定义YML文件加载
 * @author huangsheng 2019/05/06 1:53 PM
 */
@Deprecated
public class BasePropertySourcesPlaceholderConfigurerBean {

//    @Bean(name = "basePropertySourcesPlaceholderConfigurer")
    public PropertySourcesPlaceholderConfigurer yaml() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("redis.yaml"));
        configurer.setProperties(yaml.getObject());
        configurer.setIgnoreUnresolvablePlaceholders(true);
        configurer.setOrder(1);
        return configurer;
    }
}