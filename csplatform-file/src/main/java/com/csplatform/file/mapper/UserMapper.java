package com.csplatform.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csplatform.file.entities.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author WangXing
 * @Date 2025/12/28 12:54
 * @PackageName:com.csplatform.file.mapper
 * @ClassName: UserMapper
 * @Version 1.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
