package com.csplatform.file.configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author WangXing
 * @Date 2025/12/28 12:19
 * @PackageName:com.csplatform.file.configuration
 * @ClassName: Swagger3Config
 * @Version 1.0
 */

@Configuration
public class Swagger3Config
{
    @Bean
    public GroupedOpenApi PayApi()
    {
        return GroupedOpenApi.builder().group("支付微服务模块").pathsToMatch("/pay/**").build();
    }
    @Bean
    public GroupedOpenApi OtherApi()
    {
        return GroupedOpenApi.builder().group("其它微服务模块").pathsToMatch("/other/**", "/others").build();
    }
    /*@Bean
    public GroupedOpenApi CustomerApi()
    {
        return GroupedOpenApi.builder().group("客户微服务模块").pathsToMatch("/customer/**", "/customers").build();
    }*/

    @Bean
    public OpenAPI docsOpenApi()
    {
        return new OpenAPI()
                .info(new Info().title("cloud2024")
                        .description("通用设计rest")
                        .version("v1.0"))
                .externalDocs(new ExternalDocumentation()
                        .description("www.atguigu.com")
                        .url("https://yiyan.baidu.com/"));
    }
}