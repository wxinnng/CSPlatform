package com.csplatform.course.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author WangXing
 * @Date 2026/1/12 14:29
 * @PackageName:com.csplatform.course.entity.vo
 * @ClassName: UserCardSetVO
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCardSetVO {

    /**
     * cardSetID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * card_set_Id
     */
    private Long cardSetId;

    /**
     * 标题
     */
    private String title;

    /**
     * 封面
     */
    private String coverUrl;
}