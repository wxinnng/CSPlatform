package com.csplatform.course;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author WangXing
 * @Date 2026/1/10 18:47
 * @PackageName:com.csplatform
 * @ClassName: CSPlatformCourse
 * @Version 1.0
 */
@SpringBootApplication
@EnableFeignClients
@MapperScan("com.csplatform.course.mapper")
public class CSPlatformCourseSpringBootApplication {
    public static void main(String[] args) {
        SpringApplication.run(CSPlatformCourseSpringBootApplication.class, args);
    }
}
