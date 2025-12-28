package com.csplatform.file.entities;

/**
 * @Author WangXing
 * @Date 2025/12/28 12:50
 * @PackageName:com.csplatform.file.entities
 * @ClassName: User
 * @Version 1.0
 */

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data // Lombok 注解，自动生成getter/setter等方法
@TableName("user") // 指定对应数据库表名，若类名与表名符合驼峰转下划线规则可省略
public class User {
    @TableId(type = IdType.AUTO) // 指定主键，并设置策略为数据库自增
    private Long id;
    // @TableField 注解在字段名与表列名不一致时指定，符合规则可省略
    private String name;
    private Integer age;
    private String email;
}
