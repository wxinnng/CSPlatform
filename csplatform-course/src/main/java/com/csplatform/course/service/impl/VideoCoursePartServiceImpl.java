package com.csplatform.course.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.csplatform.course.entity.VideoCoursePart;
import com.csplatform.course.mapper.VideoCoursePartMapper;
import com.csplatform.course.service.VideoCoursePartService;
import org.springframework.stereotype.Service;

@Service
public class VideoCoursePartServiceImpl extends ServiceImpl<VideoCoursePartMapper, VideoCoursePart> implements VideoCoursePartService {
    // 基础的CRUD方法已由ServiceImpl实现，无需额外编码
}