package com.csplatform.course.entity;

/**
 * @Author WangXing
 * @Date 2026/1/10 19:00
 * @PackageName:com.csplatform.course.entity
 * @ClassName: Card
 * @Version 1.0
 */

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @NotNull(message = "单词集ID不能为空")
    private Long cardSetId;

    @NotBlank(message = "卡片内容不能为空")
    private String content;

    private Integer status;

    // 内容解析的便捷方法
    public CardContent parseContent() {
        return JSON.parseObject(content, CardContent.class);
    }

    public void setContentFromObject(CardContent cardContent) {
        this.content = JSON.toJSONString(cardContent);
    }
}