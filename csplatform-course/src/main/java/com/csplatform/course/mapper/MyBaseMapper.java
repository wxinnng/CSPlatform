package com.csplatform.course.mapper;

/**
 * @Author WangXing
 * @Date 2026/1/15 19:59
 * @PackageName:com.csplatform.course.mapper
 * @ClassName: MyBaseMapper
 * @Version 1.0
 */

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 自定义BaseMapper
 */
public interface MyBaseMapper<T> extends BaseMapper<T> {
    int insertBatchSomeColumn(@Param("list") List<T> entityList);
}
