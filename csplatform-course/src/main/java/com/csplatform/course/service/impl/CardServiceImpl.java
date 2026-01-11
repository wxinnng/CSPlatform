package com.csplatform.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csplatform.common.exception.BusinessException;
import com.csplatform.course.entity.Card;
import com.csplatform.course.entity.CardSet;
import com.csplatform.course.entity.UserCardSet;
import com.csplatform.course.enums.UserCardSetStatus;
import com.csplatform.course.mapper.CardMapper;
import com.csplatform.course.mapper.CardSetMapper;
import com.csplatform.course.mapper.UserCardSetMapper;
import com.csplatform.course.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @Author WangXing
 * @Date 2026/1/10 19:08
 * @PackageName:com.csplatform.course.service.impl
 * @ClassName: CardService
 * @Version 1.0
 */
@Service
public class CardServiceImpl extends ServiceImpl<CardMapper, Card> implements CardService {

    @Autowired
    private UserCardSetMapper userCardSetMapper;

    @Autowired
    private CardSetMapper cardSetMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createCardSet(CardSet cardSet) {

        //插入CardSet
        int insert = cardSetMapper.insert(cardSet);

        if(insert < 1)
            throw new BusinessException("服务器异常，请稍后再试！");

        //批量导入
        if(cardSet.getJsonData() != null && !cardSet.getJsonData().isEmpty())       {
            //TODO:插入JSON
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void studyCardSet(Long userId, Long cardSetId) {

        //1.查询参数是否正确
        CardSet cardSet = cardSetMapper.selectById(cardSetId);

        if(cardSet == null)
            throw new BusinessException("参数异常！");

        //2.封装对象,插入到数据库
        UserCardSet userCardSet = new UserCardSet().setCardSetId(cardSetId).setUserId(userId).setStatus(UserCardSetStatus.NOT_STARTED.getCode()).setJoinTime(LocalDateTime.now());
        int result = userCardSetMapper.insert(userCardSet);
        if(result < 1)
            throw new BusinessException("操作失败！");

        //3.更新cardSet信息
        cardSet.setLearningNum( cardSet.getLearningNum() + 1);
        LambdaUpdateWrapper<CardSet> cardSetLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        int update = cardSetMapper.update(cardSetLambdaUpdateWrapper);
        if(update < 1)
            throw new BusinessException("操作失败！");
    }
}
