package com.csplatform.course.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author WangXing
 * @Date 2026/1/22 13:24
 * @PackageName:com.csplatform.course.properties
 * @ClassName: OtherServerInfo
 * @Version 1.0
 */
@Data
@Component
@ConfigurationProperties("other-info")
public class OtherServerInfo {

    private String fileServerBase;

}
