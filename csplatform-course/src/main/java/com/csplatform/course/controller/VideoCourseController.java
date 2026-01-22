package com.csplatform.course.controller;

import com.csplatform.common.resp.Result;
import com.csplatform.course.entity.RelatedResource;
import com.csplatform.course.entity.VideoCourse;
import com.csplatform.course.entity.VideoCoursePart;
import com.csplatform.course.entity.vo.CreateVideoCourseVO;
import com.csplatform.course.service.VideoCoursePartService;
import com.csplatform.course.service.VideoCourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author WangXing
 * @Date 2026/1/16 15:13
 * @PackageName:com.csplatform.course.controller
 * @ClassName: VideoCourseController
 * @Version 1.0
 */
@RestController
@RequestMapping("/video")
@Slf4j
public class VideoCourseController {

    @Autowired
    private VideoCourseService videoCourseService;

    @Autowired
    private VideoCoursePartService videoCoursePartService;


    /**
     * 分页获取视频列表
     */
    @GetMapping("/get_videos")
    public Result<List<VideoCourse>> getAllVideoCourseByPage(@RequestParam("pageNum") Integer pageNum,@RequestParam("pageSize") Integer pageSize){
        log.info("pageSize : {} ,PageNum : {}",pageSize,pageNum);
        List<VideoCourse> videoCourses =  videoCourseService.getAllVideoCourse(pageSize,pageNum);
        return Result.success(videoCourses);
    }

    /**
     * 获得用户发布的所有视频
     */
    @GetMapping("/get_published_videos")
    public Result<List<VideoCourse>> getAllPublishedVideoCourse(@RequestParam("userId") Long userId){
        log.info("获得用户：{} 发布的所有课程.",userId);
        List<VideoCourse> videoCourses = videoCourseService.getAllPublishedVideoCourse(userId);
        return Result.success(videoCourses);
    }


    /**
     * 获取视频详细part
     */
    @RequestMapping("/get_video_parts")
    public Result<List<VideoCoursePart>> getAllVideoCoursePart(@RequestParam("videoCourseId") Long videoCourseId){
        log.info("视频：{} 的详细信息 ",videoCourseId);
        List<VideoCoursePart> parts = videoCourseService.getAllVideoCourseParts(videoCourseId);
        return Result.success(parts);
    }

    /**
     * 获得所有视频相关资源
     */
    @GetMapping("/get_related_resource")
    public Result<List<RelatedResource>> getAllRelatedResource(@RequestParam("videoCourseId") Long videoCourseId){
        log.info("视频ID : {} 的相关资源",videoCourseId);
        List<RelatedResource> relatedResources = videoCourseService.getAllRelatedResource(videoCourseId);
        return Result.success(relatedResources);
    }

    /**
     * 上传基本信息
     */
    @PostMapping("/post_base_info")
    public Result<Long> postBaseInfo(@RequestBody VideoCourse videoCourse){
        log.info("create-course : {}",videoCourse);
        Long id = videoCourseService.insertNewVideoCourse(videoCourse);
        return Result.success(id);
    }

    /**
     * 新建课程
     */
    @PostMapping("/create_video_course")
    public Result<String> createVideoCourse(@RequestBody CreateVideoCourseVO createVideoCourseVO){
        log.info("info :{}",createVideoCourseVO);
        videoCourseService.createVideoCourse(createVideoCourseVO);
        return Result.success("OK");
    }

}
