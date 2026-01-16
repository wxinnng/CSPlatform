package com.csplatform.course.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.csplatform.course.entity.VideoCourse;
import com.csplatform.course.mapper.VideoCourseMapper;
import com.csplatform.course.service.VideoCourseService;
import org.springframework.stereotype.Service;

@Service
public class VideoCourseServiceImpl extends ServiceImpl<VideoCourseMapper, VideoCourse> implements VideoCourseService {
    // 基础的CRUD方法已由ServiceImpl实现，无需额外编码
}