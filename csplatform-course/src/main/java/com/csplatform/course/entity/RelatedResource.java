package com.csplatform.course.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author WangXing
 * @Date 2026/1/21 11:07
 * @PackageName:com.csplatform.course.entity
 * @ClassName: RelatedResource
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "related_resource")
public class RelatedResource {

    @TableId
    private Long id;

    @Column(name = "video_course_id", nullable = false)
    private Long videoCourseId;

    @Column(nullable = false)
    private Integer type;

    @Column(name = "resource_id", nullable = false)
    private Long resourceId;

    @Column(nullable = false)
    private Integer status = 1; // 0: 删除, 1: 启用

    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name= "description")
    private String description;
}
