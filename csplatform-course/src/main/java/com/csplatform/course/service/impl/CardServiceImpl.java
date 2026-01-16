package com.csplatform.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csplatform.common.exception.BusinessException;
import com.csplatform.course.entity.Card;
import com.csplatform.course.entity.CardSet;
import com.csplatform.course.entity.UserCardSet;
import com.csplatform.course.entity.vo.CardBatchInputVO;
import com.csplatform.course.entity.vo.StudyCardVO;
import com.csplatform.course.enums.CardStatusEnum;
import com.csplatform.course.enums.UserCardSetStatus;
import com.csplatform.course.mapper.CardMapper;
import com.csplatform.course.mapper.CardSetMapper;
import com.csplatform.course.mapper.UserCardSetMapper;
import com.csplatform.course.service.CardService;
import com.csplatform.course.service.CardSetService;
import com.csplatform.course.service.UserCardSetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author WangXing
 * @Date 2026/1/10 19:08
 * @PackageName:com.csplatform.course.service.impl
 * @ClassName: CardService
 * @Version 1.0
 */
@Service
@Slf4j
public class CardServiceImpl extends ServiceImpl<CardMapper, Card> implements CardService {


    @Autowired
    private CardMapper cardMapper;

    @Autowired
    private CardSetService cardSetService;

    @Autowired
    private UserCardSetService userCardSetService;

    @Autowired
    private UserCardSetMapper userCardSetMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Card addCard(Card card) {
        // 处理imgArray转换为imgUrl：将URL列表用竖线连接
        if (card.getImgArray() != null && card.getImgArray().length != 0) {
            String imgUrl = String.join("|", card.getImgArray());
            card.setImgUrl(imgUrl);
        } else {
            card.setImgUrl(""); // 如果imgArray为空，设置空字符串
        }

        //1.插入card表中
        card.setStatus(CardStatusEnum.ENABLED.getCode());
        card.setUpdateTime(LocalDateTime.now());
        int insert = cardMapper.insert(card);
        if(insert < 1)
            throw new BusinessException("操作失败！");
        //2.update cardSet表
        cardSetService.incrCardNum(card.getCardSetId(),1);
        return card;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cardBatchInput(CardBatchInputVO cardBatchInputVO) {
        if (cardBatchInputVO == null || cardBatchInputVO.getCardList() == null
                || cardBatchInputVO.getCardList().isEmpty()) {
            log.warn("批量插入数据为空，cardBatchInputVO: {}", cardBatchInputVO);
            return;
        }

        List<CardBatchInputVO.CardItem> cardItems = cardBatchInputVO.getCardList();
        log.info("开始批量插入卡片数据，数量: {}", cardItems.size());

        try {
            // 将CardItem转换为Card实体
            List<Card> cardEntities = convertToCardEntities(cardItems, cardBatchInputVO);

            // 执行批量插入
            boolean success = executeBatchInsert(cardEntities);

            if (success) {
                log.info("批量插入卡片数据成功，共插入 {} 条记录", cardEntities.size());
                cardSetService.incrCardNum(cardEntities.get(0).getCardSetId(),cardEntities.size());
            } else {
                throw new RuntimeException("批量插入卡片数据失败");
            }

        } catch (Exception e) {
            log.error("批量插入卡片数据异常: {}", e.getMessage(), e);
            throw new RuntimeException("批量插入失败: " + e.getMessage());
        }
    }

    @Override
    public void updateCard(Card card) {

        //检测参数
        if(card.getContent().isEmpty())
            throw new BusinessException("参数异常！");

        //1.imgArray -> imgUrl
        card.setImgUrl(convertImgArrayToUrl(card.getImgArray()));

        //2.更新时间
        card.setUpdateTime(LocalDateTime.now());

        //3.构建
        LambdaUpdateWrapper<Card> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Card::getId,card.getId())
                .set(Card::getContent,card.getContent())
                .set(Card::getImgUrl,card.getImgUrl());

        //4.执行
        int update = cardMapper.update(updateWrapper);

        if(update < 1)
            throw new BusinessException("操作失败！");

    }

    /**
     * 将CardItem转换为Card实体
     */
    private List<Card> convertToCardEntities(List<CardBatchInputVO.CardItem> cardItems,
                                             CardBatchInputVO inputVO) {
        List<Card> cardList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (CardBatchInputVO.CardItem item : cardItems) {
            // 构建Card实体
            Card card = Card.builder()
                    .cardSetId(inputVO.getCardId()) // 使用VO中的cardId作为cardSetId
                    .content(item.getContent())
                    .imgUrl(convertImgArrayToUrl(item.getImgArray())) // 转换图片数组
                    .updateTime(now)
                    .status(1) // 默认启用状态
                    .build();

            // 验证必要字段
            validateCardFields(card);
            cardList.add(card);
        }

        return cardList;
    }

    /**
     * 将imgArray转换为imgUrl字符串
     */
    private String convertImgArrayToUrl(String[] imgArray) {
        if (imgArray != null && imgArray.length != 0) {
            return String.join("|", imgArray);
        }
        return ""; // 如果imgArray为空，返回空字符串
    }

    /**
     * 验证卡片必要字段
     */
    private void validateCardFields(Card card) {
        if (card.getCardSetId() == null) {
            throw new RuntimeException("卡片所属单词集ID不能为空");
        }
        if (card.getContent() == null || card.getContent().trim().isEmpty()) {
            throw new RuntimeException("卡片内容不能为空");
        }
    }

    /**
     * 执行批量插入
     */
    private boolean executeBatchInsert(List<Card> cardList) {
        if (cardList.isEmpty()) {
            return true;
        }

        try {
            // 使用MyBatis-Plus的saveBatch方法，每批1000条
            return saveBatch(cardList, 1000);
        } catch (Exception e) {
            log.error("批量插入异常: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudyCardVO startStudyCard(Long userId, Long cardSetId) {
        //1.查询cardSetjilu
        LambdaQueryWrapper<UserCardSet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCardSet::getUserId,userId)
                .eq(UserCardSet::getCardSetId,cardSetId);

        //2.查询数据
        List<UserCardSet> userCardSets = userCardSetMapper.selectList(queryWrapper);

        //3.判断正确性
        if(userCardSets.size() > 1){
            throw new BusinessException("参数错误!");
        }

        UserCardSet userCardSet = null;

        if(userCardSets.isEmpty()){
            //插入
            userCardSet = userCardSetService.studyCardSet(userId,cardSetId);
        }else{
            userCardSet = userCardSets.get(0);
        }
        //4.查询card对象
        if(userCardSet.getNowStudyCardId() == null)
            userCardSet.setNowStudyCardId(1L);


        return getNextStudyCard(cardSetId, userCardSet.getId(), 1, 1);
    }

    @Override
    @Transactional
    public StudyCardVO getNextStudyCard(Long cardSetId,Long userCardSetId ,Integer pageSize, Integer num) {


        //直接使用mapper来查
        Card card = cardMapper.selectByOrderedIndex(cardSetId,num);

        StudyCardVO studyCardVO = new StudyCardVO();

        //card == null
        if(card == null){
            //已经没有更多数据了
            studyCardVO.setIsEnd(true);
            //返回对象
            return studyCardVO;
        }


        // 4. 更新学习进度
        updateStudyRecord(userCardSetId);

        // 5. 返回数据列表
        //5.1数据整型
        parseImgUrlToArray(card);

        //封装对象
        studyCardVO.setId(card.getId());
        studyCardVO.setImgArray(card.getImgArray());
        studyCardVO.setContent(card.getContent());
        studyCardVO.setUserCardSetId(userCardSetId);
        studyCardVO.setNowStudyId(num);

        return studyCardVO;
    }


    private void updateStudyRecord(Long userCardSetId){

        //构造wrapper
        LambdaUpdateWrapper<UserCardSet> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserCardSet::getId,userCardSetId)
                .setIncrBy(UserCardSet::getNowStudyCardId,1);

        //执行update操作
        boolean update = userCardSetService.update(updateWrapper);
        if(!update)
            throw new BusinessException("操作失败！");

    }


    @Override
    public List<Card> getAllCardSByCardSetId(Long cardSetId) {
        //1. 原有的查询逻辑
        LambdaQueryWrapper<Card> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Card::getCardSetId, cardSetId)
                .eq(Card::getStatus, CardStatusEnum.ENABLED.getCode())
                .orderByAsc(Card::getUpdateTime);
        List<Card> cardList = cardMapper.selectList(queryWrapper);

        //2. 遍历查询结果，为每个Card对象解析imgUrl并填充imgArray
        for (Card card : cardList) {
            // 调用解析方法
            parseImgUrlToArray(card);
            //减少数量
            card.setImgUrl(null);
        }

        //3. 返回处理后的列表
        return cardList;
    }

    @Override
    public void deleteCard(Long cardId) {
        LambdaUpdateWrapper<Card> updateWrapper = new LambdaUpdateWrapper<>();

        updateWrapper.eq(Card::getId,cardId)
                .set(Card::getStatus,CardStatusEnum.DELETED.getCode());

        //执行
        int update = cardMapper.update(updateWrapper);
        if(update < 1)
            throw new BusinessException("操作失败！");
    }

    /**
     * 将Card对象的imgUrl字符串按竖线分隔符解析到imgArray中
     */
    private void parseImgUrlToArray(Card card) {
        // 获取imgUrl字段的值
        String imgUrl = card.getImgUrl();

        // 检查imgUrl是否为空或null
        if (imgUrl == null || imgUrl.trim().isEmpty()) {
            // 如果为空，可以设置为一个空数组，避免后续操作出现NullPointerException
            card.setImgArray(null); // 或者根据你的需求，setImgArray(null)
            return;
        }

        // 使用split方法按转义的竖线进行分割
        // 关键：竖线 | 是正则表达式元字符，需要转义成 \\|
        String[] imgArray = imgUrl.split("\\|");

        // 将分割后的数组设置回Card对象
        card.setImgArray(imgArray);
    }
}
