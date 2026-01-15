package com.csplatform.course.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author WangXing
 * @Date 2026/1/15 15:15
 * @PackageName:com.csplatform.course.entity.vo
 * @ClassName: StudyCardVO
 * @Version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyCardVO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @NotNull(message = "单词集ID不能为空")
    private Long cardSetId;

    private Long userCardSetId;

    @NotBlank(message = "卡片内容不能为空")
    private String content;


    private Integer nowStudyId;

    @TableField(exist = false)
    private String[]  imgArray;


    private Boolean isEnd = false;

}
