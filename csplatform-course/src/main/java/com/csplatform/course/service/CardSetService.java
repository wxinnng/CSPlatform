package com.csplatform.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.csplatform.course.entity.CardSet;

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
     * 分页查询知识卡片
     * @param page
     * @param size
     * @return
     */
    List<CardSet> getAllCardSet(Integer page, Integer size);
}
