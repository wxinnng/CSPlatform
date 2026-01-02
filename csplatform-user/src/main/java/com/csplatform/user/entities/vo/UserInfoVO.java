package com.csplatform.user.entities.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author WangXing
 * @Date 2026/1/1 9:35
 * @PackageName:com.csplatform.user.entities.vo
 * @ClassName: UserInfoVO
 * @Version 1.0
 */
import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户信息值对象 (Value Object)
 * 用于封装和传输用户基本信息
 */
@Data
@Accessors(chain = true)
public class UserInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 性别：0-未知 1-男 2-女
     */
    private Integer gender;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 学习等级
     */
    private String learningLevel;

    /**
     * 用户角色
     */
    private List<String> role;

    /**
     * 账户状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 背景
     */
    private String backgroundUrl;

    /**
     * 密码
     */
    private String password;
}
