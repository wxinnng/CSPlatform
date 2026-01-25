package com.csplatform.chat.entities.vo;

import lombok.Data;

import java.util.List;

@Data
public class MessageVO {
    /**
     * 日期分组（时间戳）
     */
    private Long date;

    /**
     * 该日期下的消息列表
     */
    private List<MessageItem> msgs;

    @Data
    public static class MessageItem {
        private Long id;
        private Long sendUser;
        private String content;
        private Integer category;
        private Long createTime;
    }
}
