package com.csplatform.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.csplatform.course.entity.CardSet;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * @Author WangXing
 * @Date 2026/1/11 16:41
 * @PackageName:com.csplatform.course.service
 * @ClassName: CardSetService
 * @Version 1.0
 */

public interface CardSetService extends IService<CardSet> {
    /**
     * 创建知识卡片
     * @param cardSet
     */
    void createCardSet(CardSet cardSet);



    /**
     * 获得所有的用户发布的卡片
     * @param userId
     * @return
     */
    List<CardSet> getAllPublishedCards(Long userId);


    /**
     * 分页查询知识卡片
     * @param page
     * @param size
     * @return
     */
    List<CardSet> getAllCardSet(Integer page, Integer size);

    /**
     * 更新CardNum
     * @param cardSetId
     */
    void incrCardNum(@NotNull(message = "单词集ID不能为空") Long cardSetId);

    /**
     * 减少CardNum
     * @param cardSetId
     */
    void decrCardNum(@NotNull Long cardSetId);
}
