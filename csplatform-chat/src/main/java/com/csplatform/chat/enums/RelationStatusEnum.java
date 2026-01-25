package com.csplatform.chat.enums;

import lombok.Getter;

/**
 * 关系状态枚举
 */
@Getter
public enum RelationStatusEnum {
    ENABLED(1, "启用"),
    DELETED(2, "删除");

    private final Integer code;
    private final String description;

    RelationStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static RelationStatusEnum getByCode(Integer code) {
        for (RelationStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public static boolean isValid(Integer code) {
        return getByCode(code) != null;
    }
}