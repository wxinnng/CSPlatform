package com.csplatform.course.service.impl;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.csplatform.common.exception.BusinessException;
import com.csplatform.course.api.FileService;
import com.csplatform.course.entity.FileInfo;
import com.csplatform.course.entity.RelatedResource;
import com.csplatform.course.entity.VideoCourse;
import com.csplatform.course.entity.VideoCoursePart;
import com.csplatform.course.entity.vo.CreateVideoCourseVO;
import com.csplatform.course.enums.RelatedResourceType;
import com.csplatform.course.enums.VideoCourseStatus;
import com.csplatform.course.mapper.RelatedResourceMapper;
import com.csplatform.course.mapper.VideoCourseMapper;
import com.csplatform.course.properties.OtherServerInfo;
import com.csplatform.course.service.VideoCoursePartService;
import com.csplatform.course.service.VideoCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class VideoCourseServiceImpl extends ServiceImpl<VideoCourseMapper, VideoCourse> implements VideoCourseService {

    @Autowired
    private VideoCourseMapper videoCourseMapper;

    @Autowired
    private OtherServerInfo otherServerInfo;

    @Autowired
    private VideoCoursePartService videoCoursePartService;

    @Autowired
    private FileService fileService;

    @Autowired
    private RelatedResourceMapper relatedResourceMapper;

    @Override
    public List<VideoCoursePart> getAllVideoCourseParts(Long videoCourseId) {

        //1.先查询是否有这个课程
        VideoCourse videoCourse = videoCourseMapper.selectById(videoCourseId);
        if(videoCourse == null || Objects.equals(videoCourse.getStatus(), VideoCourseStatus.DELETE.getCode()))
            throw new BusinessException("参数异常！");

        //2.如果有这个课程，继续操作
        LambdaQueryWrapper<VideoCoursePart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VideoCoursePart::getVideoCourseId, videoCourseId)
                .eq(VideoCoursePart::getStatus, VideoCourseStatus.USING.getCode())
                .orderByAsc(VideoCoursePart::getPartNum); // 升序排列

        //返回
        return videoCoursePartService.list(wrapper);

    }

    @Override
    public Long insertNewVideoCourse(VideoCourse videoCourse) {
        if (videoCourse == null)
            throw new BusinessException("参数错误");

        videoCourse.setStatus(VideoCourseStatus.USING.getCode());
        videoCourse.setCreateTime(LocalDateTime.now());
        videoCourse.setUpdateTime(LocalDateTime.now());
        videoCourse.setLearningNum(0);
        videoCourse.setPartNum(0);

        int insert = videoCourseMapper.insert(videoCourse);

        if(insert < 1)
            throw new BusinessException("操作失败！");
        return videoCourse.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createVideoCourse(CreateVideoCourseVO createVideoCourseVO) {

        //1.先插入基本信息
        VideoCourse videoCourse = new VideoCourse();
        videoCourse.setUserId(createVideoCourseVO.getUserId());
        videoCourse.setCoverUrl(createVideoCourseVO.getCoverUrl());
        videoCourse.setPartNum(createVideoCourseVO.getEpisodes().size());
        videoCourse.setCategory(createVideoCourseVO.getCategory());
        videoCourse.setTitle(createVideoCourseVO.getTitle());
        videoCourse.setDescription(createVideoCourseVO.getDescription());
        Long courseId = insertNewVideoCourse(videoCourse);

        //2.插入分集信息
        createVideoCourseVO.getEpisodes().forEach((item) -> {

            FileInfo data = fileService.getFileInfoById((item.getId())).getData();
            VideoCoursePart videoCoursePart = new VideoCoursePart();
            videoCoursePart.setVideoCourseId(courseId);
            videoCoursePart.setPartNum(item.getOrder());
            videoCoursePart.setDuration(item.getDuration());
            videoCoursePart.setVideoUrl(otherServerInfo.getFileServerBase() + data.getFilePath());
            videoCoursePart.setStatus(VideoCourseStatus.USING.getCode());
            videoCoursePart.setCreateTime(LocalDateTime.now());
            videoCoursePart.setUpdateTime(LocalDateTime.now());
            videoCoursePart.setTitle(item.getName());

            boolean saved = videoCoursePartService.save(videoCoursePart);

            if(!saved)
                throw new BusinessException("操作失败");
        });

    }

    @Override
    public List<RelatedResource> getAllRelatedResource(Long videoCourseId) {
        //构造wrapper
        LambdaQueryWrapper<RelatedResource> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(RelatedResource::getVideoCourseId,videoCourseId)
                .orderByAsc(RelatedResource::getType);

        //查询
        return relatedResourceMapper.selectList(queryWrapper);
    }

    @Override
    public List<VideoCourse> getAllPublishedVideoCourse(Long userId) {

        //封装参数
        LambdaQueryWrapper<VideoCourse> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(VideoCourse::getUserId,userId)
                .eq(VideoCourse::getStatus,VideoCourseStatus.USING.getCode());

        //查询
        return videoCourseMapper.selectList(queryWrapper);

    }

    @Override
    public List<VideoCourse> getAllVideoCourse(Integer pageSize, Integer pageNum) {
        //1.构造对象
        Page<VideoCourse> page = Page.of(pageNum, pageSize);

        //2.wrapper
        LambdaQueryWrapper<VideoCourse> lambdaQueryWrapper = Wrappers.lambdaQuery();

        //3.执行
        Page<VideoCourse> videoCoursePage = page(page, lambdaQueryWrapper);

        //4.返回
        return videoCoursePage.getRecords();
    }
}