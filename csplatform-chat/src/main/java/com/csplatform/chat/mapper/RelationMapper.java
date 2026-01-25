package com.csplatform.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csplatform.chat.entities.Relation;
import com.csplatform.chat.entities.vo.ContactVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author WangXing
 * @Date 2026/1/23 13:17
 * @PackageName:com.csplatform.chat.mapper
 * @ClassName: ChatMapper
 * @Version 1.0
 */
@Mapper
public interface RelationMapper extends BaseMapper<Relation> {


    /**
     * 获取用户的好友列表及最后一条消息
     * @param userId 当前用户ID
     * @return 联系人列表
     */
    List<ContactVO> selectContactsWithLastMessage(@Param("userId") Long userId);

}
