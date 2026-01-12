package com.csplatform.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csplatform.course.entity.UserCardSet;
import com.csplatform.course.entity.vo.UserCardSetVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @Author WangXing
 * @Date 2026/1/10 19:06
 * @PackageName:com.csplatform.course.mapper
 * @ClassName: UserCardSetMapper
 * @Version 1.0
 */
@Mapper
public interface UserCardSetMapper extends BaseMapper<UserCardSet> {

    /**
     * 通过 user_card_set 表的 cardSetId 与 card_set 表的 id 关联查询
     * 根据用户ID查询关联的单词集列表
     * @param userId 用户ID
     * @return 关联的单词集列表
     */
    List<UserCardSetVO> selectLinkedCardSetsByUserId(@Param("userId") Long userId);

    @Update("delete from user_card_set where id = #{id};")
    int removeCard(@Param("id")  Long id);
}