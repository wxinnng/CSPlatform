package com.csplatform.common.exception;

/**
 * @Author WangXing
 * @Date 2025/12/30 15:55
 * @PackageName:com.csplatform.common.exception
 * @ClassName: BusinessException
 * @Version 1.0
 */


import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}