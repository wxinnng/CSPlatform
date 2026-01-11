package com.csplatform.course.controller;

import com.csplatform.common.exception.BusinessException;
import com.csplatform.common.resp.Result;
import com.csplatform.course.entity.CardSet;
import com.csplatform.course.service.CardService;
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


    /**
     * 用户学习某个知识卡片
     */
    @GetMapping("/study_card_set")
    public Result<String> studyCardSet(@RequestParam("userId") Long userId,@RequestParam("cardSetId") Long cardSetId){
        log.info("用户：{} 学习 知识卡片{}",userId,cardSetId);
        cardService.studyCardSet(userId,cardSetId);
        return Result.success("操作成功!");
    }


    /**
     * 创建知识卡片
     */
    @PostMapping("/create_card_set")
    public Result<String> createCardSet(@RequestBody CardSet cardSet){
        log.info("card: {}",cardSet);
        cardService.createCardSet(cardSet);
        return Result.success("操作成功！");
    }



}
