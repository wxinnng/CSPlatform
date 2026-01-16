package com.csplatform.course.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
@TableName("video_course")
public class VideoCourse implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("title")
    private String title;

    @TableField("userId")
    private Long userId;

    @TableField("partNum")
    private Integer partNum;

    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "updateTime", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField("status")
    private Integer status;

    @TableField("description")
    private String description;

    @TableField("learning_num")
    private Integer learningNum;

    @TableField("category")
    private String category;

    @TableField("cover_url")
    private String coverUrl;
}