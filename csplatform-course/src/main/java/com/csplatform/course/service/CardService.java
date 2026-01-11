package com.csplatform.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.csplatform.course.entity.Card;
import com.csplatform.course.entity.CardSet;

/**
 * @Author WangXing
 * @Date 2026/1/10 19:07
 * @PackageName:com.csplatform.course.service
 * @ClassName: CardService
 * @Version 1.0
 */

public interface CardService extends IService<Card> {

    /**
     * 用户学习知识卡片
     * @param userId
     * @param cardSetId
     */
    void studyCardSet(Long userId, Long cardSetId);

    /**
     * 创建知识卡片
     * @param cardSet
     */
    void createCardSet(CardSet cardSet);
}
