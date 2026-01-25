package com.csplatform.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csplatform.chat.entities.Message;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author WangXing
 * @Date 2026/1/23 13:18
 * @PackageName:com.csplatform.chat.mapper
 * @ClassName: MessageMapper
 * @Version 1.0
 */

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
