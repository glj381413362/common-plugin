package com.common.plugin.lock.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * description：用来加载自定义的配置文件
 * 注意： 需要在resources/META-INF/spring.factories文件中配置，以达到自动实例话的效果
 *
 * @author roman 2019/05/31 5:28 PM
 */
public class CustomerEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private final YamlPropertySourceLoader loader = new YamlPropertySourceLoader();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                       SpringApplication application) {

        //===============================================================================
        //  支持多个配置文件的加载（只需要指定配置文件路径和对应的资源名【必须唯一不然可能会被覆盖】）
        //===============================================================================
        Resource path = new ClassPathResource("redisson.yaml");
        List<ResourceExpand> resourceExpandList = new ArrayList<>();
        resourceExpandList.add(
                new ResourceExpand().setName("redisson-custom-resource").setPath(path)
        );
        //===============================================================================
        //  加载配置未见为配置对象
        //===============================================================================
        List<PropertySource<?>> propertySources = loadYaml(resourceExpandList);
        for (PropertySource<?> propertySource : propertySources) {
            environment.getPropertySources().addLast(propertySource);
        }
    }

    private PropertySource<?> loadYaml(ResourceExpand resourceExpand) {
        Resource path = resourceExpand.getPath();
        if (!path.exists()) {
            throw new IllegalArgumentException("Resource " + path + " does not exist");
        }
        try {
            return this.loader.load(resourceExpand.getName(), path).get(0);
        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Failed to load yaml configuration from " + path, ex);
        }
    }

    /**
     * 加载配置文件到PropertySource对象
     * @param resourceExpands
     * @return
     */
    public List<PropertySource<?>> loadYaml(List<ResourceExpand> resourceExpands) {
        List<PropertySource<?>> propertySourceList = new ArrayList<>();
        for (ResourceExpand resourceExpand : resourceExpands) {
            propertySourceList.add(loadYaml(resourceExpand));
        }
        return propertySourceList;
    }

    /**
     * 方便封装Resource以其对应的名字
     */
    class ResourceExpand {
        Resource path;
        String name;

        public Resource getPath() {
            return path;
        }

        public ResourceExpand setPath(Resource path) {
            this.path = path;
            return this;
        }

        public String getName() {
            return name;
        }

        public ResourceExpand setName(String name) {
            this.name = name;
            return this;
        }
    }

}
