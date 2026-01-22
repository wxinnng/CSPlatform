package com.csplatform.course.enums;

/**
 * @Author WangXing
 * @Date 2026/1/20 16:38
 * @PackageName:com.csplatform.course.enums
 * @ClassName: VideoCourseStatus
 * @Version 1.0
 */

public enum VideoCourseStatus {
    DELETE(0, "已删除"),
    USING(1, "开放中"),
    WAITING(2, "等待审核中");

    private final Integer code;
    private final String desc;

    VideoCourseStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() { return code; }
    public String getDesc() { return desc; }
}
