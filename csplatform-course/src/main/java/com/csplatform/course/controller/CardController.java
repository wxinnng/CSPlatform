package com.csplatform.course.controller;

import com.csplatform.common.exception.BusinessException;
import com.csplatform.common.resp.Result;
import com.csplatform.course.entity.Card;
import com.csplatform.course.entity.CardSet;
import com.csplatform.course.entity.vo.CardBatchInputVO;
import com.csplatform.course.entity.vo.StudyCardVO;
import com.csplatform.course.entity.vo.UserCardSetVO;
import com.csplatform.course.service.CardService;
import com.csplatform.course.service.CardSetService;
import com.csplatform.course.service.UserCardSetService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author WangXing
 * @Date 2026/1/10 19:06
 * @PackageName:com.csplatform.course.controller
 * @ClassName: CardController
 * @Version 1.0
 */
@RestController
@RequestMapping("/card")
@Slf4j
public class CardController {


    @Autowired
    private CardService cardService;

    @Autowired
    private CardSetService cardSetService;

    @Autowired
    private UserCardSetService userCardSetService;

    /**
     * 用户学习某个知识卡片
     */
    @GetMapping("/study_card_set")
    public Result<String> studyCardSet(@RequestParam("userId") Long userId,@RequestParam("cardSetId") Long cardSetId){
        log.info("用户：{} 学习 知识卡片{}",userId,cardSetId);
        userCardSetService.studyCardSet(userId,cardSetId);
        return Result.success("操作成功!");
    }


    /**
     * 创建知识卡片
     */
    @PostMapping("/create_card_set")
    public Result<String> createCardSet(@RequestBody CardSet cardSet){
        log.info("card: {}",cardSet);
        cardSetService.createCardSet(cardSet);
        return Result.success("操作成功！");
    }

    /**
     * 获得所有的cardSet
     */
    @GetMapping("/all_card_set")
    public Result<List<CardSet>> getAllCardSet(@RequestParam("page") Integer page,
                                               @RequestParam("size") Integer size){
        log.info("获得所有的cardSet");
        List<CardSet> result = cardSetService.getAllCardSet(page,size);
        return Result.success(result);
    }

    /**
     * 获得某个用户所有的学习的卡片
     */
    @GetMapping("/get_learning_cards")
    public Result<List<UserCardSetVO>> getAllLearningCards(@RequestParam("userId") Long userId){
        log.info("获得用户：{} 的所有学习卡片",userId);
        List<UserCardSetVO> cards = userCardSetService.getAllLearningCards(userId);
        return Result.success(cards);
    }


    /**
     * 获得所有用户发布的卡片
     */
    @GetMapping("/get_published_cards")
    public Result<List<CardSet>> getAllPublishedCards(@RequestParam("userId") Long userId){
        log.info("获得用户:{} 所有的发布的卡片",userId);
        List<CardSet> cardSetVOS = cardSetService.getAllPublishedCards(userId);
        return Result.success(cardSetVOS);
    }

    /**
     * 删除当前学习内容
     */
    @GetMapping("/delete_learning_card")
    public Result<String> deleteLearningCardById(@RequestParam("id") Long id){
        log.info("删除ID: {}", id);
        userCardSetService.deleteLearningCardById(id);
        return Result.success("删除成功！");
    }

    /**
     * 插入一个卡片
     */
    @PostMapping("/add_card")
    public Result<Card> addCard(@RequestBody Card card){
        log.info("card:{}",card);
        return Result.success(cardService.addCard(card));
    }

    /**
     * 获得所有的卡片
     */
    @GetMapping("/get_cards")
    public Result<List<Card>> getAllCards(@RequestParam("cardSetId") Long cardSetId){
        log.info("cardSetId: {}",cardSetId);
        List<Card> cards = cardService.getAllCardSByCardSetId(cardSetId);
        return Result.success(cards);
    }


    /**
     * 删除card
     */
    @GetMapping("/delete_card")
    public Result<String> deleteCard(@RequestParam("cardId") Long cardId){
        log.info("删除的CardId:{}",cardId);
        cardService.deleteCard(cardId);
        return Result.success("OK");
    }

    /**
     * 学习卡片
     */
    @GetMapping("/next_card")
    public Result<StudyCardVO> nextCard(@RequestParam("cardSetId") Long cardSetId,
                                 @NotNull @RequestParam("userCardSetId") Long userCardSetId,
                                 @RequestParam("pageSize") Integer pageSize,
                                 @NotNull @RequestParam("pageNum") Integer pageNum){
        log.info("学习卡片:cardSetId {} ,num : {}", cardSetId ,pageNum);
        StudyCardVO nextCard = cardService.getNextStudyCard(cardSetId,userCardSetId,pageSize,pageNum);
        return Result.success(nextCard);
    }

    /**
     * 开始学习卡片
     */
    @GetMapping("/start_study_card")
    public Result<StudyCardVO> startStudyCard(@RequestParam("userId")Long userId, @RequestParam("cardSetId") Long cardSetId){
        log.info("学习卡片 userId: {},cardSetId :{}",userId,cardSetId);
        return Result.success(cardService.startStudyCard(userId,cardSetId));
    }

    /**
     * 批量导入card
     */
    @PostMapping("/input_cards_batch")
    public Result<String> inputCardsBatch(@RequestBody CardBatchInputVO cardBatchInputVO){
        log.info("批量插入的数据: {}",cardBatchInputVO);
        cardService.cardBatchInput(cardBatchInputVO);
        return Result.success("OK");
    }

    /**
     * 修改卡片
     */
    @PostMapping("/update_card")
    public Result<String> updateCard(@RequestBody Card card){
        log.info("修改卡片信息 : {}",card);
        cardService.updateCard(card);
        return Result.success("OK");
    }



}

