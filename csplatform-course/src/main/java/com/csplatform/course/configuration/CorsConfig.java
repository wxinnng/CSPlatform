package com.csplatform.course.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 配置所有API路径
                // 选择以下其中一行配置，推荐使用 allowedOriginPatterns
                .allowedOriginPatterns("*")    // 【推荐】Spring Boot 2.4+
                // .allowedOrigins("*")          // 【传统】Spring Boot 2.4 之前
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的请求方法
                .allowedHeaders("*")             // 允许所有请求头
                .allowCredentials(true)          // 允许携带认证信息（如Cookies）
                .maxAge(3600);                   // 预检请求缓存时间（秒）
    }
}