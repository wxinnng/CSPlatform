package com.csplatform.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.csplatform.course.entity.UserCardSet;
import com.csplatform.course.entity.vo.UserCardSetVO;

import java.util.List;

/**
 * @Author WangXing
 * @Date 2026/1/12 8:40
 * @PackageName:com.csplatform.course.service
 * @ClassName: UserCardSetService
 * @Version 1.0
 */

public interface UserCardSetService extends IService<UserCardSet> {

    /**
     * 用户学习知识卡片
     * @param userId
     * @param cardSetId
     */
    void studyCardSet(Long userId, Long cardSetId);

    /**
     * 获得用户所有的在学习中的卡片
     * @return
     */
    List<UserCardSetVO> getAllLearningCards(Long userId);


    /**
     * 通过ID删除学习记录
     * @param id
     */
    void deleteLearningCardById(Long id);
}
