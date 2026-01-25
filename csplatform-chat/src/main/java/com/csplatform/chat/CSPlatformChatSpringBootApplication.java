package com.csplatform.chat;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author WangXing
 * @Date 2025/12/29 9:25
 * @PackageName:com.csplatform.chat
 * @ClassName: CSPlatformChatSpringBootApplication
 * @Version 1.0
 */

@SpringBootApplication
@MapperScan("com.csplatform.chat.mapper")
@EnableFeignClients
public class CSPlatformChatSpringBootApplication {
    public static void main(String[] args) {
        SpringApplication.run(CSPlatformChatSpringBootApplication.class,args);
    }
}
