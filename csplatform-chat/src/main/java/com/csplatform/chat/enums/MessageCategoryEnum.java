package com.csplatform.chat.enums;

import lombok.Getter;

/**
 * 消息类型枚举
 */
@Getter
public enum MessageCategoryEnum {
    TEXT(1, "文字"),
    IMAGE(2, "图片"),
    EMOJI(3, "表情"),
    FILE(4, "文件");

    private final Integer code;
    private final String description;

    MessageCategoryEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static MessageCategoryEnum getByCode(Integer code) {
        for (MessageCategoryEnum value : values()) {
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