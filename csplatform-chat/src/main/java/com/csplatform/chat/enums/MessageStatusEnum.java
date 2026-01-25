package com.csplatform.chat.enums;

import lombok.Getter;

/**
 * 消息状态枚举
 */
@Getter
public enum MessageStatusEnum {
    RECEIVED(1, "已接收"),
    UNREAD(2, "未查看"),
    FAILED(3, "发送失败");

    private final Integer code;
    private final String description;

    MessageStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static MessageStatusEnum getByCode(Integer code) {
        for (MessageStatusEnum value : values()) {
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