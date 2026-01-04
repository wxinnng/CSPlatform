package com.csplatform.file;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author WangXing
 * @Date 2025/12/28 12:09
 * @PackageName:com.csplatform.file
 * @ClassName: CSPlatformFileSpringBootApplication
 * @Version 1.0
 */
@SpringBootApplication
@MapperScan("com.csplatform.file.mapper")
public class CSPlatformFileSpringBootApplication {
    public static void main(String[] args) {
        SpringApplication.run(CSPlatformFileSpringBootApplication.class, args);
    }
}
