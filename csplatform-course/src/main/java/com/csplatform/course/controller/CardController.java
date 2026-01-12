package com.csplatform.course.controller;

import com.csplatform.common.exception.BusinessException;
import com.csplatform.common.resp.Result;
import com.csplatform.course.entity.CardSet;
import com.csplatform.course.entity.vo.UserCardSetVO;
import com.csplatform.course.service.CardService;
import com.csplatform.course.service.CardSetService;
import com.csplatform.course.service.UserCardSetService;
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
     * 删除当前学习内容
     */
    @GetMapping("/delete_learning_card")
    public Result<String> deleteLearningCardById(@RequestParam("id") Long id){
        log.info("删除ID: {}", id);
        userCardSetService.deleteLearningCardById(id);
        return Result.success("删除成功！");
    }

}
