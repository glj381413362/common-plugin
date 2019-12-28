package com.enhance.config;

import com.enhance.core.filter.AddTraceIdFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * <p>
 *
 * </p>
 *
 * @author gongliangjun 2019/07/01 11:18
 */
@Configuration
public class EnableLogChainConfiguration {
	@Bean
	public FilterRegistrationBean<AddTraceIdFilter> addTraceIdFilterRegistration() {
		FilterRegistrationBean<AddTraceIdFilter> filterRegistrationBean =
				new FilterRegistrationBean<>(addTraceIdFilter());
		filterRegistrationBean.setUrlPatterns(Collections.singleton("/v1/**"));
		return filterRegistrationBean;
	}

	@Bean
	public AddTraceIdFilter addTraceIdFilter() {
		return new AddTraceIdFilter();
	}

}
