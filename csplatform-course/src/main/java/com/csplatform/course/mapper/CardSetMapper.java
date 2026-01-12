package com.csplatform.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csplatform.course.entity.CardSet;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @Author WangXing
 * @Date 2026/1/10 19:05
 * @PackageName:com.csplatform.course.mapper
 * @ClassName: CardSetMapper
 * @Version 1.0
 */
@Mapper
public interface CardSetMapper extends BaseMapper<CardSet> {

    @Update("update card_set set learning_num = learning_num - 1 where id = #{cardSetId}")
    int decreaseOne(@Param("cardSetId") Long cardSetId);
}
