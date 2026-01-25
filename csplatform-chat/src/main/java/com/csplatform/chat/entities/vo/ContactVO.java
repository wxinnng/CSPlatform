package com.csplatform.chat.entities.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author WangXing
 * @Date 2026/1/23 13:57
 * @PackageName:com.csplatform.chat.entities.vo
 * @ClassName: ContactVO
 * @Version 1.0
 */
@Data
public class ContactVO {

    private Long id;

    private String lastMessage;

    private LocalDateTime lastTime;

    private Integer category;

    private String userName;

    private Boolean isOnline;

    private String avatarUrl;

    private Integer unRead;
}
