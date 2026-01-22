package com.csplatform.course.service;

/**
 * @Author WangXing
 * @Date 2026/1/16 13:04
 * @PackageName:com.csplatform.course.service
 * @ClassName: VideoCourseService
 * @Version 1.0
 */


import com.baomidou.mybatisplus.extension.service.IService;
import com.csplatform.course.entity.RelatedResource;
import com.csplatform.course.entity.VideoCourse;
import com.csplatform.course.entity.VideoCoursePart;
import com.csplatform.course.entity.vo.CreateVideoCourseVO;

import java.util.List;

public interface VideoCourseService extends IService<VideoCourse> {


    /**
     * 获得所有的视频课程
     * @param pageSize
     * @param pageNum
     * @return
     */
    List<VideoCourse> getAllVideoCourse(Integer pageSize, Integer pageNum);

    /**
     *
     * @param videoCourseId
     * @return
     */
    List<VideoCoursePart> getAllVideoCourseParts(Long videoCourseId);

    /**
     * 通过UserId，获得所有当前用户发布的视频课程
     * @param userId
     * @return
     */
    List<VideoCourse> getAllPublishedVideoCourse(Long userId);

    /**
     * 获得所有的视频相关资源
     * @param
     * @return
     */
    List<RelatedResource> getAllRelatedResource(Long videoCourseId);

    Long insertNewVideoCourse(VideoCourse videoCourse);

    /**
     * 创建视频课程
     * @param createVideoCourseVO
     */
    void createVideoCourse(CreateVideoCourseVO createVideoCourseVO);
}