package com.csplatform.course.enums;

/**
 * @Author WangXing
 * @Date 2026/1/21 11:11
 * @PackageName:com.csplatform.course.enums
 * @ClassName: RelatedResourceType
 * @Version 1.0
 */

public enum RelatedResourceType {

    FILE(1,"文件"),
    CARD(2,"卡片"),
    WORD(3,"文字课程"),
    URL(4,"链接");

    private final Integer code;
    private final String desc;

    RelatedResourceType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() { return code; }
    public String getDesc() { return desc; }
}
