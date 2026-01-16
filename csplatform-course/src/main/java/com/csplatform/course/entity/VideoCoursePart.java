package com.csplatform.course.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
/**
 * @Author WangXing
 * @Date 2026/1/16 13:02
 * @PackageName:com.csplatform.course.entity
 * @ClassName: VideoCoursePart
 * @Version 1.0
 */


@Data
@TableName("video_course_part")
public class VideoCoursePart implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("video_course_id")
    private Long videoCourseId;

    @TableField("part_num")
    private Integer partNum;

    @TableField("title")
    private String title;

    @TableField("video_url")
    private String videoUrl;

    @TableField("duration")
    private Integer duration;

    @TableField("status")
    private Integer status;

    @TableField(value = "updateTime", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}