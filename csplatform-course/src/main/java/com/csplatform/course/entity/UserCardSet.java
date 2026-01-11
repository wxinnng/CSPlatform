package com.csplatform.course.entity;

/**
 * @Author WangXing
 * @Date 2026/1/10 19:02
 * @PackageName:com.csplatform.course.entity
 * @ClassName: UserCardSet
 * @Version 1.0
 */

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserCardSet {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "单词集ID不能为空")
    private Long cardSetId;

    private LocalDateTime joinTime;

    private Long nowStudyCardId;

    private Integer status;
}
