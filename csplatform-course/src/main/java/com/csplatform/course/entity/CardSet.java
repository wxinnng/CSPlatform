package com.csplatform.course.entity;

/**
 * @Author WangXing
 * @Date 2026/1/10 19:00
 * @PackageName:com.csplatform.course.entity
 * @ClassName: CardSet
 * @Version 1.0
 */

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardSet {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @NotNull(message = "创建者ID不能为空")
    private Long userId;

    @NotBlank(message = "单词集标题不能为空")
    private String title;

    private LocalDateTime createTime;

    private Integer cardNum;

    private String coverUrl;

    private Integer category;

    private Integer status;

    private Integer learningNum;

    private Integer description;

    private LocalDateTime updateTime;


}