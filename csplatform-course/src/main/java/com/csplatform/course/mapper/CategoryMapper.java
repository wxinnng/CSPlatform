package com.csplatform.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csplatform.course.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author WangXing
 * @Date 2026/1/11 13:41
 * @PackageName:com.csplatform.course.mapper
 * @ClassName: CategoryMapper
 * @Version 1.0
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

}
