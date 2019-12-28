package com.common.plugin.sms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * description
 *
 * @author roman 2019/06/05 11:17 PM
 */

@Configuration
@ConfigurationProperties(prefix = "sms.ali")
public class AliSmsConfig {
    private String defaultConnectTimeout;
    private String defaultReadTimeout;
    private String accessKeyId;
    private String accessKeySecret;
    private String product;
    private String domain;

    public String getDefaultConnectTimeout() {
        return defaultConnectTimeout;
    }

    public AliSmsConfig setDefaultConnectTimeout(String defaultConnectTimeout) {
        this.defaultConnectTimeout = defaultConnectTimeout;
        return this;
    }

    public String getDefaultReadTimeout() {
        return defaultReadTimeout;
    }

    public AliSmsConfig setDefaultReadTimeout(String defaultReadTimeout) {
        this.defaultReadTimeout = defaultReadTimeout;
        return this;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public AliSmsConfig setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
        return this;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public AliSmsConfig setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
        return this;
    }

    public String getProduct() {
        return product;
    }

    public AliSmsConfig setProduct(String product) {
        this.product = product;
        return this;
    }

    public String getDomain() {
        return domain;
    }

    public AliSmsConfig setDomain(String domain) {
        this.domain = domain;
        return this;
    }
}
