package com.csplatform.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csplatform.user.entities.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author WangXing
 * @Date 2025/12/30 15:53
 * @PackageName:com.csplatform.user.mapper
 * @ClassName: UserMapper
 * @Version 1.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
