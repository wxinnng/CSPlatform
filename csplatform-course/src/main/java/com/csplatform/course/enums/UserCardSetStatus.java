package com.csplatform.course.enums;

/**
 * @Author WangXing
 * @Date 2026/1/10 19:03
 * @PackageName:com.csplatform.course.enums
 * @ClassName: UserCardSetStatus
 * @Version 1.0
 */

public enum UserCardSetStatus {
    NOT_STARTED(0, "未开始"),
    STUDYING(1, "学习中"),
    COMPLETED(2, "已完成"),
    PAUSED(3, "暂停");

    private final Integer code;
    private final String desc;

    UserCardSetStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() { return code; }
    public String getDesc() { return desc; }
}
