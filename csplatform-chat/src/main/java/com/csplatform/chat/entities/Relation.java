package com.csplatform.chat.entities;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author WangXing
 * @Date 2026/1/23 13:04
 * @PackageName:com.csplatform.chat.entities
 * @ClassName: Relation
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("relation")
public class Relation {

    @TableId
    private Long id;

    private Long userA;

    private Long userB;

    private LocalDateTime createTime;

    private Integer status; //1. 启用   2.删除
}
