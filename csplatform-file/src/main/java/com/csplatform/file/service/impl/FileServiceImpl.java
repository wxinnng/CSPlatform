package com.csplatform.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csplatform.common.exception.BusinessException;
import com.csplatform.file.api.UserService;
import com.csplatform.file.entities.vo.FolderVO;
import com.csplatform.file.enums.FileCategoryEnums;
import com.csplatform.file.enums.FileDelFlagEnums;
import com.csplatform.file.constant.MessageConstant;
import com.csplatform.file.entities.FileInfo;
import com.csplatform.file.entities.vo.FileVO;
import com.csplatform.file.enums.FileStatus;
import com.csplatform.file.enums.UploadStatus;
import com.csplatform.file.mapper.FileMapper;
import com.csplatform.file.properties.MinioPropertie;
import com.csplatform.file.service.FileService;
import com.csplatform.file.util.MinioUtils;
import com.csplatform.file.util.RedisUtil;
import com.csplatform.file.util.StringUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @Author WangXing
 * @Date 2026/1/3 17:24
 * @PackageName:com.csplatform.file.service.impl
 * @ClassName: FileServiceImpl
 * @Version 1.0
 */
@Service
@Slf4j
public class FileServiceImpl extends ServiceImpl<FileMapper, FileInfo> implements FileService {

    @Autowired
    private FileMapper fileInfoMapper;

    @Autowired
    private MinioUtils minioUtils;

    @Autowired
    private MinioPropertie minioProperties;

    @Autowired
    private UserService userService;

    @Autowired
    private ThreadPoolTaskExecutor executor;

    @Resource
    private RedisUtil redisUtil;
    @Override
    public String uploadSmallFile(MultipartFile file, FileInfo fileInfo) {

        if(file == null)
            throw new BusinessException("文件为空！");

        //最终返回的path
        String url = minioProperties.getEndpoint();

        //使用minio上传文件
        String path = minioUtils.uploadFile(minioProperties.getBucketName(), Objects.requireNonNull(file.getOriginalFilename()), file);

        if(fileInfo != null){
            //TODO:把信息写入数据库

        }

        url = url + "/" + path;

        log.info("文件url : {}", url);

        return url;
    }

    /**
     * 上传文件方法。
     * 该方法负责检查文件是否已存在，如果存在，则返回已存在标志；如果不存在且是完整文件，则上传文件到MinIO并保存文件信息到数据库。
     *
     * @param fileVO 文件相关信息VO，包含文件本身、MD5、文件名等。
     * @return 如果文件已存在，返回秒传状态码；如果文件上传完成，返回上传完成状态码；否则返回null。
     * @throws BusinessException 如果文件为空，抛出通用异常。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public HashMap<Object, Object> uploadFile(FileVO fileVO) throws ExecutionException, InterruptedException {

        // 判断文件是否为空
        if(fileVO.getFile().isEmpty())
            throw new BusinessException("文件上传异常");

        FileInfo insertItem = new FileInfo();
        LocalDateTime now = LocalDateTime.now();
        HashMap<Object, Object> map = new HashMap<>();

        // 第一片文件
        if(fileVO.getChunkNumber() == 1){
            // 先去数据库看看有没有这个文件
            QueryWrapper<FileInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("file_md5", fileVO.getFileMd5());

            List<FileInfo> fileInfoList = fileInfoMapper.selectList(queryWrapper);
            FileInfo fileInfo = null;
            if(!fileInfoList.isEmpty()){
                fileInfo = fileInfoList.get(0);
            }

            // 别人已经上传过这个文件了，直接秒传
            if(fileInfo != null){
                log.info("服务器中有相同的文件，直接秒传");

                insertItem.setUserId(fileVO.getUserId());
                insertItem.setFileMd5(fileVO.getFileMd5());
                insertItem.setFileName(fileInfo.getFileName());
                insertItem.setFileCategory(fileInfo.getFileCategory());
                insertItem.setFileId(StringUtil.getRandomString(10));
                insertItem.setDelFlag(FileDelFlagEnums.USING.getFlag());
                insertItem.setFilePid(fileVO.getFilePid());
                insertItem.setFilePath(fileInfo.getFilePath());
                insertItem.setCreateTime(now);
                insertItem.setFileSize(fileInfo.getFileSize());
                insertItem.setState(UploadStatus.UPLOAD_FINISH.getStatus());

                fileInfoMapper.insert(insertItem);
                log.info("秒传文件插入成功: {}", insertItem);

                afterLoadFile(fileVO);

                map.put("status", UploadStatus.UPLOAD_FINISH.getStatus());
                map.put("fileId", insertItem.getFileId());
                return map;
            }
        }

        // 只有一段，直接上传
        if(fileVO.getChunkTotal() == 1){
            int lastDotIndex = fileVO.getFileName().lastIndexOf(".");
            String type = lastDotIndex > 0 ?
                    fileVO.getFileName().substring(lastDotIndex + 1) : "";

            // 上传文件到MinIO
            CompletableFuture<String> asyncUploadFileResult = CompletableFuture.supplyAsync(() -> {
                try {
                    return minioUtils.uploadFile(MessageConstant.MINIO_BUCKET, fileVO.getFileName(), fileVO.getFile());
                } catch (Exception e) {
                    log.error("文件上传失败: {}", e.getMessage(), e);
                    throw new RuntimeException("文件上传失败", e);
                }
            }, executor).whenCompleteAsync((result, exception) -> {
                if(exception != null){
                    log.error("上传文件失败", exception);
                } else {
                    insertItem.setUserId(fileVO.getUserId());
                    insertItem.setFileMd5(fileVO.getFileMd5());
                    insertItem.setFileName(fileVO.getFileName());
                    insertItem.setFileCategory(FileCategoryEnums.getByCode(type).getCategory());
                    insertItem.setFileId(StringUtil.getRandomString(10));
                    insertItem.setDelFlag(FileDelFlagEnums.USING.getFlag());
                    insertItem.setFilePid(fileVO.getFilePid());
                    insertItem.setFilePath(result);
                    insertItem.setCreateTime(now);
                    insertItem.setFileSize(fileVO.getFile().getSize());
                    insertItem.setState(UploadStatus.UPLOAD_FINISH.getStatus());

                    try {
                        fileInfoMapper.insert(insertItem);
                        log.info("单文件上传成功: {}", insertItem.getFileName());
                    } catch (Exception e) {
                        log.error("文件信息保存失败", e);
                    }
                }
            });
            //多线程：
            //循环更新文件大小
            afterLoadFile(fileVO);
            // 删除redis中的键
            redisUtil.del(fileVO.getFileMd5());
            map.put("status", UploadStatus.UPLOAD_FINISH.getStatus());
            map.put("fileId", insertItem.getFileId());

            return map;
        }

        // 检查Redis中是否有这个MD5的键
        Object redisValue = redisUtil.get(fileVO.getFileMd5());
        if (redisValue == null) {
            // 如果Redis中没有，可能是第一次上传但chunkNumber != 1，或者Redis键过期
            // 重新设置并继续上传
            redisUtil.set(fileVO.getFileMd5(), 0);
            log.warn("Redis中未找到MD5键: {}，重新设置为0", fileVO.getFileMd5());
        } else {
            // 检查分片是否已上传
            int uploadedChunkCount = Integer.parseInt(redisValue.toString());
            if(uploadedChunkCount >= fileVO.getChunkNumber()){
                // 说明这片文件已经上传过了
                log.info("分片已上传: MD5={}, chunk={}, redis={}",
                        fileVO.getFileMd5(), fileVO.getChunkNumber(), uploadedChunkCount);
                map.put("status", UploadStatus.UPLOADING.getStatus());
                return map;
            }
            //没有上传，更新
            redisUtil.set(fileVO.getFileMd5(), fileVO.getChunkNumber());
        }


        // 分片上传
        log.info("分片上传: MD5={}, chunk={}/{}, fileName={}",
                fileVO.getFileMd5(), fileVO.getChunkNumber(), fileVO.getChunkTotal(), fileVO.getFileName());

        String objectName = fileVO.getUserId() + "_" + fileVO.getFileMd5();

        try {
            minioUtils.putChunkObject(fileVO.getFile().getInputStream(),
                    MessageConstant.MINIO_BUCKET,
                    objectName + "/" + fileVO.getChunkNumber());
            log.info("分片上传成功: chunk={}", fileVO.getChunkNumber());
        } catch (IOException e) {
            log.error("分片上传失败", e);
            throw new BusinessException("文件上传异常！");
        }

        // 最后一片，进行合并
        if(Objects.equals(fileVO.getChunkNumber(), fileVO.getChunkTotal())){
            int lastDotIndex = fileVO.getFileName().lastIndexOf(".");
            String type = lastDotIndex > 0 ?
                    fileVO.getFileName().substring(lastDotIndex + 1) : "";

            // 合并文件
            String filePath = minioUtils.composeObject(MessageConstant.MINIO_BUCKET,
                    MessageConstant.MINIO_BUCKET, objectName, type);

            if (filePath == null || filePath.isEmpty()) {
                throw new BusinessException("文件合并失败");
            }

            insertItem.setUserId(fileVO.getUserId());
            insertItem.setFileMd5(fileVO.getFileMd5());
            insertItem.setFileName(fileVO.getFileName());
            insertItem.setFileCategory(FileCategoryEnums.getByCode(type).getCategory());
            insertItem.setFileId(StringUtil.getRandomString(10));
            insertItem.setDelFlag(FileDelFlagEnums.USING.getFlag());
            insertItem.setFilePid(fileVO.getFilePid());
            insertItem.setFilePath(filePath);
            insertItem.setCreateTime(now);
            Long fileSize = MessageConstant.DEFAULT_CHUNK_SIZE * (fileVO.getChunkTotal() - 1) + fileVO.getFile().getSize();
            insertItem.setFileSize(fileSize);
            insertItem.setState(UploadStatus.UPLOAD_FINISH.getStatus());

            // 保存文件信息
            int insertResult = fileInfoMapper.insert(insertItem);
            log.info("文件信息保存结果: {}, fileId: {}", insertResult, insertItem.getFileId());

            // 清理临时文件
            try {
                boolean deleteResult = minioUtils.deleteFolder(MessageConstant.MINIO_BUCKET, objectName);
                log.info("临时文件清理结果: {}", deleteResult);
            } catch (Exception e) {
                log.warn("临时文件清理失败", e);
            }

            //多线程：
            //循环更新文件大小
            afterLoadFile(fileVO);

            // 删除Redis键
            redisUtil.del(fileVO.getFileMd5());

            map.put("status", UploadStatus.UPLOAD_FINISH.getStatus());
            map.put("fileId", insertItem.getFileId());

            return map;
        }

        // 更新Redis中的上传进度
        redisUtil.set(fileVO.getFileMd5(), fileVO.getChunkNumber());
        map.put("status", UploadStatus.UPLOADING.getStatus());

        return map;
    }


    /**
     * 上传文件后执行的后置操作
     * @param fileVO
     */
    private void afterLoadFile(FileVO fileVO) {
        // 异步更新文件占用空间（循环更新父目录）
        executor.execute(() -> {
            String pid = fileVO.getFilePid();
            Long totalSize = fileVO.getTotalSize();
            LambdaUpdateWrapper<FileInfo> updateWrapper = new LambdaUpdateWrapper<>();

            // 循环更新所有父目录的大小，直到根目录（pid=0）
            while (pid != null && !"0".equals(pid)) {
                // 设置更新条件：file_size = file_size + totalSize
                updateWrapper.setSql("file_size = file_size + " + totalSize)
                        .eq(FileInfo::getFileId, pid);

                // 执行更新
                fileInfoMapper.update(null, updateWrapper);

                // 获取当前目录的父目录ID，继续向上更新
                FileInfo currentDir = fileInfoMapper.selectById(pid);
                if (currentDir != null) {
                    pid = currentDir.getFilePid();
                } else {
                    break;
                }

                // 清空wrapper以便下次使用
                updateWrapper.clear();
            }
        });

        // 异步更新用户总使用空间
        executor.execute(() -> {
            userService.updateUserFileSpace(fileVO.getUserId(), fileVO.getTotalSize());
        });
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public FolderVO getRootFolderByUserId(Long id) {

        //查询root
        LambdaQueryWrapper<FileInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.
                eq(FileInfo::getUserId,id)
                .eq(FileInfo::getDelFlag, FileDelFlagEnums.USING.getFlag())
                .eq(FileInfo::getFilePid,"0");

        //查询
        FileInfo fileInfo = fileInfoMapper.selectOne(queryWrapper);

        //如果fileInfo为空
        if(fileInfo == null){
            //创建一个对象
            fileInfo = new FileInfo();

            //创建一个目录
            fileInfo.setUserId(id);
            fileInfo.setCreateTime(LocalDateTime.now());
            fileInfo.setFileCategory(FileCategoryEnums.FOLDER.getCategory());
            fileInfo.setFilePid("0");
            fileInfo.setState(3);
            fileInfo.setDelFlag(2);
            fileInfo.setFileMd5(StringUtil.getRandomString(20));
            fileInfo.setFileId(StringUtil.getRandomString(10));

            int insert = fileInfoMapper.insert(fileInfo);
            if(insert < 0)
                throw  new BusinessException("初始化错误！");
        }

        //拿到根目录ID
        LambdaQueryWrapper<FileInfo> queryWrapper1 = new LambdaQueryWrapper<>();

        queryWrapper1.eq(FileInfo::getFilePid,fileInfo.getFileId())
                .eq(FileInfo::getDelFlag,FileDelFlagEnums.USING.getFlag())
                .orderBy(true,true,FileInfo::getCreateTime);

        FolderVO folderVO = new FolderVO();

        //封装信息
        folderVO.setChildren(fileInfoMapper.selectList(queryWrapper1));
        folderVO.setFolderInfo(fileInfo);

        //返回信息
        return folderVO;
    }

    @Override
    public FileInfo getFileInfoByFileId(String id) {
        FileInfo fileInfo = fileInfoMapper.selectById(id);
        if(fileInfo == null)
            throw new BusinessException("参数错误！");
        return fileInfo;
    }

    @Override
    public void initFileRoot(Long id) {
        //初始化文件目录
        initRoot(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restoreFiles(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }


        for (String id : ids) {
            // 1. 查询要恢复的文件/文件夹
            FileInfo fileInfo = fileInfoMapper.selectById(id);
            if (fileInfo == null) {
                throw new BusinessException("文件不存在: " + id);
            }

            // 2. 检查文件是否在回收站
            if (!Objects.equals(fileInfo.getDelFlag(), FileDelFlagEnums.RECYCLE.getFlag())) {
                throw new BusinessException("文件不在回收站: " + fileInfo.getFileName());
            }

            Long userId = fileInfo.getUserId();

            // 3. 检查父文件夹状态
            String parentId = fileInfo.getFilePid();
            FileInfo parentFolder = null;

            LambdaUpdateWrapper<FileInfo> updateWrapper = new LambdaUpdateWrapper<>();
            LambdaQueryWrapper<FileInfo> queryWrapper = new LambdaQueryWrapper<>();



            // 如果不是根目录，查询父文件夹
            while (!"0".equals(parentId)) {

                //查询用户父文件夹信息
                queryWrapper.eq(FileInfo::getDelFlag,FileDelFlagEnums.USING.getFlag())
                        .eq(FileInfo::getFileId,parentId);
                parentFolder = fileInfoMapper.selectOne(queryWrapper);

                //清空
                queryWrapper.clear();

                //如果父文件夹已经不存在了
                if(! Objects.equals(parentFolder.getDelFlag(),FileDelFlagEnums.USING.getFlag())){
                    //root作为父文件夹,执行update语句
                    //1.查询根目录，用户ID查询：userId和filePid == ‘0’
                    LambdaQueryWrapper<FileInfo> rootQuery = new LambdaQueryWrapper<>();
                    rootQuery.eq(FileInfo::getUserId, userId)
                            .eq(FileInfo::getFilePid, "0")
                            .eq(FileInfo::getDelFlag, FileDelFlagEnums.USING.getFlag())
                            .last("LIMIT 1");
                    FileInfo rootFolder = fileInfoMapper.selectOne(rootQuery);

                    //2.设置为当前目录的父文件夹(update语句)
                    updateWrapper.set(FileInfo::getFilePid, rootFolder.getFileId())
                            .eq(FileInfo::getFileId, fileInfo.getFileId());

                    int result = fileInfoMapper.update(null, updateWrapper);
                    if (result == 0) {
                        throw new BusinessException("更新文件父目录失败");
                    }

                    //3.把设置为根目录
                    parentFolder.setFileId(rootFolder.getFileId());
                }

                //更新父目录大小，加
                updateWrapper.setSql("file_size = file_size + " + fileInfo.getFileSize())
                        .eq(FileInfo::getFileId, parentFolder.getFileId());
                fileInfoMapper.update(updateWrapper);

                //清空
                updateWrapper.clear();

                parentId = parentFolder.getFilePid();
            }

            if (Objects.equals(fileInfo.getFileCategory(), FileCategoryEnums.FOLDER.getCategory())) {
                restoreFolderRecursive(fileInfo, fileInfo.getFilePid());
            } else {
                // 恢复单个文件
                restoreSingleFile(fileInfo, fileInfo.getFilePid());
            }
        }
    }



    /**
     * 恢复单个文件
     */
    private void restoreSingleFile(FileInfo fileInfo, String newParentId) {
        Long fileSize = 0L;
        // 更新文件状态
        LambdaUpdateWrapper<FileInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(FileInfo::getDelFlag, FileDelFlagEnums.USING.getFlag())
                .set(FileInfo::getFilePid, newParentId)
                .eq(FileInfo::getFileId, fileInfo.getFileId())
                .eq(FileInfo::getDelFlag, FileDelFlagEnums.RECYCLE.getFlag());

        int result = fileInfoMapper.update(null, updateWrapper);
        if (result == 0) {
            throw new BusinessException("文件恢复失败: " + fileInfo.getFileName());
        }


    }

    /**
     * 递归恢复文件夹及其内容
     */
    private void restoreFolderRecursive(FileInfo folderInfo, String newParentId) {

        // 1. 先恢复当前文件夹
        restoreSingleFile(folderInfo, newParentId);

        // 2. 查找文件夹下的所有子项
        LambdaQueryWrapper<FileInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FileInfo::getFilePid, folderInfo.getFileId())
                .eq(FileInfo::getDelFlag, FileDelFlagEnums.RECYCLE.getFlag());

        List<FileInfo> children = fileInfoMapper.selectList(queryWrapper);

        // 3. 递归恢复子项
        for (FileInfo child : children) {
            if (Objects.equals(child.getFileCategory(), FileCategoryEnums.FOLDER.getCategory())) {
                // 子文件夹：递归恢复
                restoreFolderRecursive(child, folderInfo.getFileId());
            } else {
                // 文件：直接恢复
                restoreSingleFile(child, folderInfo.getFileId());
            }
        }
    }

    /**
     * 更新父文件夹大小
     */
    private void updateParentFolderSize(String fileId, Long size) {
        if (StringUtils.isBlank(fileId) || "0".equals(fileId) || size == null || size == 0) {
            return;
        }

        FileInfo parentFolder = fileInfoMapper.selectById(fileId);
        if (parentFolder == null ||
                !Objects.equals(parentFolder.getFileCategory(), FileCategoryEnums.FOLDER.getCategory())) {
            return;
        }

        // 更新文件夹大小
        Long newSize = (parentFolder.getFileSize() != null ? parentFolder.getFileSize() : 0L) + size;
        LambdaUpdateWrapper<FileInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(FileInfo::getFileSize, newSize)
                .eq(FileInfo::getFileId, fileId);

        fileInfoMapper.update(null, updateWrapper);

        // 递归向上更新
        updateParentFolderSize(parentFolder.getFilePid(), size);
    }


    @Override
    public List<FileInfo> getRecycleFolder(String id) {
        // 1. 首先查询回收站中的所有文件和文件夹
        LambdaQueryWrapper<FileInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FileInfo::getFilePid, id)
                .eq(FileInfo::getDelFlag, FileDelFlagEnums.RECYCLE.getFlag())
                .orderByDesc(FileInfo::getCreateTime);

        return fileInfoMapper.selectList(queryWrapper);
    }

    @Override
    public void deleteFilesBatch(List<String> ids) {
        //1.总大小
        Long totalSize = 0L;

        Map<String, Long> stringLongMap = new HashMap<>();

        for (String id : ids) {
            stringLongMap = deleteFiles(id);
            totalSize += stringLongMap.get("size");
        }


        // 使用总大小
        log.info("总共释放空间：" + totalSize + " 字节");

        userService.updateUserFileSpace(stringLongMap.get("userId"),totalSize * (-1));

    }

    @Override
    public List<FileInfo> getRecycleFiles(Long userId) {
        // 1. 首先查询回收站中的所有文件和文件夹
        LambdaQueryWrapper<FileInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FileInfo::getUserId, userId)
                .eq(FileInfo::getDelFlag, FileDelFlagEnums.RECYCLE.getFlag())
                .orderByDesc(FileInfo::getCreateTime);

        List<FileInfo> allRecycled = fileInfoMapper.selectList(queryWrapper);

        // 2. 收集所有文件夹的ID
        Set<String> folderIds = allRecycled.stream()
                .filter(file -> file.getFileCategory() == 4) // 4 表示文件夹
                .map(FileInfo::getFileId)
                .collect(Collectors.toSet());

        // 3. 过滤掉那些父文件夹也在回收站中的文件
        List<FileInfo> result = allRecycled.stream()
                .filter(file -> {
                    // 如果是文件夹，总是显示
                    if (file.getFileCategory() == 4) {
                        return true;
                    }

                    // 如果是文件，检查其父文件夹是否也在回收站中
                    // 如果父文件夹不在回收站中，则显示这个文件
                    return !folderIds.contains(file.getFilePid());
                })
                .collect(Collectors.toList());

        return result;
    }

    /**
     * 删除文件,相关操作
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String,Long> deleteFiles(String id) {

        Map<String,Long> result = new HashMap<>();

        // 1. 查找要回收的文件/文件夹
        FileInfo fileInfo = fileInfoMapper.selectById(id);

        if (fileInfo == null) {
            throw new BusinessException("文件不存在");
        }
        result.put("userId",fileInfo.getUserId());

        // 2. 判断是否是文件夹
        if (fileInfo.getFileCategory() == 4) {
            // 文件夹：递归回收文件夹及其所有内容
            result.put("size",deleteFolderRecursive(fileInfo));
        } else {
            result.put("size",deleteSingleFile(fileInfo));
        }

        return result;
    }
    private Long deleteSingleFile(FileInfo fileInfo) {

        Long size = 0L;

        LambdaUpdateWrapper<FileInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(FileInfo::getDelFlag, FileDelFlagEnums.DEL.getFlag())
                .eq(FileInfo::getFileId, fileInfo.getFileId())
                .eq(FileInfo::getDelFlag, FileDelFlagEnums.RECYCLE.getFlag()); // 防止重复回收

        //为了回收重复，只回收文件，不回收文件夹
        if(!Objects.equals(fileInfo.getFileCategory(), FileCategoryEnums.FOLDER.getCategory())){
            size = fileInfo.getFileSize();
        }

        int result = fileInfoMapper.update(null, updateWrapper);
        if (result == 0) {
            throw new BusinessException("文件已被删除");
        }
        return size;
    }
    private Long deleteFolderRecursive(FileInfo fileInfo) {

        Long size = 0L;

        // 1. 先回收当前文件夹
        deleteSingleFile(fileInfo);

        // 2. 查找文件夹下的所有文件和子文件夹
        LambdaQueryWrapper<FileInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FileInfo::getFilePid, fileInfo.getFilePid())
                .eq(FileInfo::getDelFlag, FileDelFlagEnums.RECYCLE.getFlag()); // 只处理未删除的

        List<FileInfo> children = fileInfoMapper.selectList(queryWrapper);

        // 3. 递归处理每个子项
        for (FileInfo child : children) {
            if (child.getFileCategory() == 4) {
                // 子文件夹：递归回收
                size += deleteFolderRecursive(fileInfo);
            } else {
                // 文件：直接回收
                size += deleteSingleFile(fileInfo);
            }
        }
        return size;
    }


    /**
     * 递归回收文件夹及其所有内容
     */
    @Override
    public void recycleFiles(List<String> ids) {
        //调用回收文件的方法
        ids.forEach(this::recycleFile);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recycleFile(String id) {
        // 1. 查找要回收的文件/文件夹
        FileInfo fileInfo = fileInfoMapper.selectById(id);
        if (fileInfo == null) {
            throw new BusinessException("文件不存在");
        }

        // 2. 判断是否是文件夹
        if (fileInfo.getFileCategory() == 4) {
            // 文件夹：递归回收文件夹及其所有内容
            recycleFolderRecursive(id);
        } else {
            // 文件：直接设置删除标志
            recycleSingleFile(id);
        }
        LambdaUpdateWrapper<FileInfo> updateWrapper = new LambdaUpdateWrapper<>();

        String pid = fileInfo.getFilePid();

        // 循环更新所有父目录的大小，直到根目录（pid=0）
        while (pid != null && !"0".equals(pid)) {
            // 设置更新条件：file_size = file_size + totalSize
            updateWrapper.setSql("file_size = file_size - " + fileInfo.getFileSize())
                    .eq(FileInfo::getFileId, pid);

            // 执行更新
            fileInfoMapper.update(null, updateWrapper);

            // 获取当前目录的父目录ID，继续向上更新
            FileInfo currentDir = fileInfoMapper.selectById(pid);
            if (currentDir != null) {
                pid = currentDir.getFilePid();
            } else {
                break;
            }

            // 清空wrapper以便下次使用
            updateWrapper.clear();
        }
    }
    private void recycleFolderRecursive(String folderId) {
        // 1. 先回收当前文件夹
        recycleSingleFile(folderId);

        // 2. 查找文件夹下的所有文件和子文件夹
        LambdaQueryWrapper<FileInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FileInfo::getFilePid, folderId)
                .eq(FileInfo::getDelFlag, FileDelFlagEnums.USING.getFlag()); // 只处理未删除的

        List<FileInfo> children = fileInfoMapper.selectList(queryWrapper);

        // 3. 递归处理每个子项
        for (FileInfo child : children) {
            if (child.getFileCategory() == 4) {
                // 子文件夹：递归回收
                recycleFolderRecursive(child.getFileId());
            } else {
                // 文件：直接回收
                recycleSingleFile(child.getFileId());
            }
        }
    }
    /**
     * 回收单个文件（设置del_flag=1）
     */
    private void recycleSingleFile(String fileId) {
        LambdaUpdateWrapper<FileInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(FileInfo::getDelFlag, 1)
                .eq(FileInfo::getFileId, fileId)
                .eq(FileInfo::getDelFlag, FileDelFlagEnums.USING.getFlag()); // 防止重复回收

        int result = fileInfoMapper.update(null, updateWrapper);
        if (result == 0) {
            throw new BusinessException("文件回收失败，可能已被删除");
        }
    }

    /**
     * 初始化root
     * @param id
     */
    private void initRoot(Long id){
        //创建一个对象
        FileInfo fileInfo = new FileInfo();

        //创建一个目录
        fileInfo.setUserId(id);
        fileInfo.setCreateTime(LocalDateTime.now());
        fileInfo.setFileCategory(FileCategoryEnums.FOLDER.getCategory());
        fileInfo.setFilePid("0");
        fileInfo.setState(3);
        fileInfo.setDelFlag(2);
        fileInfo.setFileId(StringUtil.getRandomString(10));
        fileInfo.setFileMd5(StringUtil.getRandomString(20));
        fileInfo.setFileSize(0L);
        fileInfo.setFileName("root");

        int insert = fileInfoMapper.insert(fileInfo);
        if(insert < 0)
            throw  new BusinessException("初始化错误！");
    }

    @Override
    public void newFolder(FileInfo folder) {
        //完善信息
        if(folder == null)
            throw new BusinessException("参数错误！");
        folder.setFileMd5(UUID.randomUUID().toString());
        folder.setFileId(StringUtil.getRandomString(10));
        folder.setDelFlag(FileDelFlagEnums.USING.getFlag());
        folder.setFileCategory(FileCategoryEnums.FOLDER.getCategory());
        folder.setState(FileStatus.USING.getStatus());
        folder.setCreateTime(LocalDateTime.now());

        //插入
        int result = fileInfoMapper.insert(folder);

        if(result < 1)
            throw new BusinessException("操作失败，请稍后再试！");
    }

    @Override
    public List<FileInfo> getFolderById(String id) {

        //先查询是否有这个父目录
        FileInfo fileInfo = fileInfoMapper.selectById(id);

        if(fileInfo == null)
            throw new BusinessException("参数错误！");

        //构造wrapper
        LambdaQueryWrapper<FileInfo> fileInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        fileInfoLambdaQueryWrapper.eq(FileInfo::getFilePid,id)
                .eq(FileInfo::getDelFlag,FileDelFlagEnums.USING.getFlag())
                .orderBy(true,true,FileInfo::getCreateTime);

        //返回对象
        return fileInfoMapper.selectList(fileInfoLambdaQueryWrapper);
    }
}
