package com.csplatform.user;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;

/**
 * @Author WangXing
 * @Date 2025/12/29 9:20
 * @PackageName:com.csplatform.user
 * @ClassName: CSPlatformUserSpringBootApplication
 * @Version 1.0
 */
@SpringBootApplication
@MapperScan("com.csplatform.user.mapper")
public class CSPlatformUserSpringBootApplication {
    public static void main(String[] args) {
        SpringApplication.run(CSPlatformUserSpringBootApplication.class,args);
    }
}
