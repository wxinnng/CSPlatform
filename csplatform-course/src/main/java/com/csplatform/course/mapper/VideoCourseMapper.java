package com.csplatform.course.mapper;

/**
 * @Author WangXing
 * @Date 2026/1/16 13:03
 * @PackageName:com.csplatform.course.mapper
 * @ClassName: VideoCourseMapper
 * @Version 1.0
 */
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csplatform.course.entity.VideoCourse;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VideoCourseMapper extends BaseMapper<VideoCourse> {
    // MyBatis-Plus已经提供了基础的CRUD方法，无需额外定义
}