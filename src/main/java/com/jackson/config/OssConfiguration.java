package com.jackson.config;

import com.jackson.properties.AliOssProperties;
import com.jackson.utils.AliOssUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OssConfiguration {

    /**
     * 配置AliOssUtils成为IOC容器中的bean
     *
     * @param aliOssProperties
     * @return
     */
    @Bean
    @ConditionalOnMissingBean  // 当IOC容器中有AliOssUtils的bean时,不再创建AliOssUtils的bean
    public AliOssUtils aliOssUtils(AliOssProperties aliOssProperties) {
        return new AliOssUtils(
                aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName()
        );
    }
}
