package com.csplatform.user.entities.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户登录请求参数
 *
 * @author WangXing
 * @version 1.0
 */
@Data
public class LoginDTO {

    /**
     * 用户邮箱地址
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 用户密码
     * 长度必须在6-20个字符之间
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;

    /**
     * 记住我选项
     * 默认为false。若为true，通常会使认证令牌有更长的有效期
     */
    private Boolean rememberMe = false;

    /**
     * 登录时需要的图形验证码（如需）
     */
    private String captcha;

    /**
     * 验证码对应的唯一Key，用于服务端验证
     */
    private String captchaKey;
}