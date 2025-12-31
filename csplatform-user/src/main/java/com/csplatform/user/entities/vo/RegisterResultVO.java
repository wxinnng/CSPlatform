package com.csplatform.user.entities.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author WangXing
 * @Date 2025/12/31 9:34
 * @PackageName:com.csplatform.user.entities.vo
 * @ClassName: RegisterResultVO
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
public class RegisterResultVO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户邮箱
     */
    private String email;
}
