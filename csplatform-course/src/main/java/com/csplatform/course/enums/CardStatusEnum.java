package com.csplatform.course.enums;

/**
 * @Author WangXing
 * @Date 2026/1/10 19:03
 * @PackageName:com.csplatform.course.enums
 * @ClassName: CardStatusEnum
 * @Version 1.0
 */

public enum CardStatusEnum {
    DISABLED(0, "禁用"),
    ENABLED(1, "启用"),
    DELETED(2, "删除");

    private final Integer code;
    private final String desc;

    CardStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() { return code; }
    public String getDesc() { return desc; }
}
