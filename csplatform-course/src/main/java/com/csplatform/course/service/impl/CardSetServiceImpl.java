package com.csplatform.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csplatform.common.exception.BusinessException;
import com.csplatform.course.entity.CardSet;
import com.csplatform.course.mapper.CardSetMapper;
import com.csplatform.course.service.CardService;
import com.csplatform.course.service.CardSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author WangXing
 * @Date 2026/1/11 16:43
 * @PackageName:com.csplatform.course.service.impl
 * @ClassName: CardSetServiceImpl
 * @Version 1.0
 */
@Service
public class CardSetServiceImpl extends ServiceImpl<CardSetMapper, CardSet> implements CardSetService {

    @Autowired
    private CardSetMapper cardSetMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createCardSet(CardSet cardSet) {

        //设置时间
        cardSet.setCreateTime(LocalDateTime.now());
        cardSet.setUpdateTime(LocalDateTime.now());

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
    public List<CardSet> getAllCardSet(Integer page, Integer size) {  // 修正参数名

        // 1. 创建分页对象
        Page<CardSet> cardSetPage = Page.of(page, size);

        // 2. 构建查询条件
        LambdaQueryWrapper<CardSet> lambdaQueryWrapper = Wrappers.lambdaQuery();

        // 3. 执行分页查询
        Page<CardSet> resultPage = page(cardSetPage, lambdaQueryWrapper);

        // 4. 返回数据列表
        return resultPage.getRecords();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrCardNum(Long cardSetId) {
        //1.wrapper
        LambdaUpdateWrapper<CardSet> updateWrapper = new LambdaUpdateWrapper<>();

        //2.设置参数
        updateWrapper.eq(CardSet::getId,cardSetId)
                .set(CardSet::getUpdateTime,LocalDateTime.now())
                .setIncrBy(CardSet::getCardNum,1);

        //3.执行
        int update = cardSetMapper.update(updateWrapper);
        if(update < 1)
            throw new BusinessException("操作失败！");
    }

    @Override
    public void decrCardNum(Long cardSetId) {
        //1.wrapper
        LambdaUpdateWrapper<CardSet> updateWrapper = new LambdaUpdateWrapper<>();

        //2.设置参数
        updateWrapper.eq(CardSet::getId,cardSetId)
                .set(CardSet::getUpdateTime,LocalDateTime.now())
                .setDecrBy(CardSet::getCardNum,1);

        //3.执行
        int update = cardSetMapper.update(updateWrapper);
        if(update < 1)
            throw new BusinessException("操作失败！");
    }

    @Override
    public List<CardSet> getAllPublishedCards(Long userId) {
        //1.构造查询条件
        LambdaQueryWrapper<CardSet> cardSetQuery = new LambdaQueryWrapper<>();
        cardSetQuery.eq(CardSet::getUserId,userId);
        return cardSetMapper.selectList(cardSetQuery);
    }



}
