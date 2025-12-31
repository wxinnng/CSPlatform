package com.csplatform.user.entities.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @Author WangXing
 * @Date 2025/12/31 9:40
 * @PackageName:com.csplatform.user.entities.dto
 * @ClassName: RegisterDTO
 * @Version 1.0
 */
@Data
public class RegisterDTO {


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
     * 邮箱验证码
     */
    @NotBlank(message = "验证码不能为空")
    @Size(min = 6,max = 6, message = "验证码长度必须为六位")
    private Integer code;

    /**
     * 是否同意用户协议
     */
    @NotBlank(message =  "必须同意用户协议")
    private Boolean isAgreed;


}