package com.csplatform.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.csplatform.course.entity.Card;
import com.csplatform.course.entity.CardSet;
import com.csplatform.course.entity.vo.StudyCardVO;

import java.util.List;

/**
 * @Author WangXing
 * @Date 2026/1/10 19:07
 * @PackageName:com.csplatform.course.service
 * @ClassName: CardService
 * @Version 1.0
 */

public interface CardService extends IService<Card> {


    /**
     * 往：cardSet里添加卡片
     * @param card
     */
    Card addCard(Card card);

    /**
     * 通过CardSetID获得所有的card
     * @param cardSetId
     * @return
     */
    List<Card> getAllCardSByCardSetId(Long cardSetId);

    /**
     * 删除Card
     * @param cardId
     */
    void deleteCard(Long cardId);

    /**
     *
     * 学习下一张card
     * @param cardSetId
     * @param pageSize
     * @param pageNum
     * @return
     */
    StudyCardVO getNextStudyCard(Long cardSetId,Long userCardSetId, Integer pageSize, Integer pageNum);

    /**
     * 开始学习卡片
     * @param userId
     * @param cardSetId
     * @return
     */
    StudyCardVO startStudyCard(Long userId, Long cardSetId);
}
