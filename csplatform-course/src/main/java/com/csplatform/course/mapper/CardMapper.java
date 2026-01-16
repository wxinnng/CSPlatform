package com.csplatform.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csplatform.course.entity.Card;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author WangXing
 * @Date 2026/1/10 19:05
 * @PackageName:com.csplatform.course.mapper
 * @ClassName: CardMapper
 * @Version 1.0
 */
@Mapper
public interface CardMapper extends MyBaseMapper<Card> {

    Card selectByOrderedIndex(@Param("cardSetId") Long cardSetId, @Param("num") Integer num);

}
