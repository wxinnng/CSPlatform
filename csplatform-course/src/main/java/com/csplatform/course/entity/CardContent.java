package com.csplatform.course.entity;

/**
 * @Author WangXing
 * @Date 2026/1/10 19:01
 * @PackageName:com.csplatform.course.entity
 * @ClassName: CardContent
 * @Version 1.0
 */

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardContent {
    private String word;
    private String phonetic;
    private String definition;
    private String exampleSentence;
    private String imageUrl;
    private String audioUrl;
    private String partOfSpeech;
    private String synonyms;
    private String antonyms;
}
