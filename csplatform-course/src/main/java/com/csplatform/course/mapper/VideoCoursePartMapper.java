package com.csplatform.course.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csplatform.course.entity.VideoCoursePart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VideoCoursePartMapper extends BaseMapper<VideoCoursePart> {
    // MyBatis-Plus已经提供了基础的CRUD方法，无需额外定义
}
