package com.csplatform.course.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author WangXing
 * @Date 2026/1/15 19:39
 * @PackageName:com.csplatform.course.entity.vo
 * @ClassName: CardBatchInputVO
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardBatchInputVO {

    private Long cardId;

    private Long userId;

    private List<CardItem> cardList;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CardItem{
        private String content;
        private String[] imgArray;
    }
}
