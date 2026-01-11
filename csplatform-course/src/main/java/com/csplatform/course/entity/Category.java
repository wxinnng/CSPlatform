package com.csplatform.course.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author WangXing
 * @Date 2026/1/11 10:34
 * @PackageName:com.csplatform.course.entity
 * @ClassName: Category
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @TableId(value = "id", type = IdType.AUTO)
    private  Long id;

    private String content;

    private LocalDateTime createTime;

    private Integer cardNum;

    private Integer videoCourseNum;
}
