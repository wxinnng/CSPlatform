package com.csplatform.file.configuration;


import com.csplatform.file.properties.MinioPropertie;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author:WangXing
 * @DATE:2024/6/4
 */
@Configuration
public class MinioConfig {

    @Autowired
    private MinioPropertie minioProperties;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(),minioProperties.getSecretKey())
                .build();
    }
}
