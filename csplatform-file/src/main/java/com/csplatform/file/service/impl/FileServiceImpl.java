package com.csplatform.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csplatform.common.exception.BusinessException;
import com.csplatform.file.entities.vo.FolderVO;
import com.csplatform.file.enums.FileCategoryEnums;
import com.csplatform.file.enums.FileDelFlagEnums;
import com.csplatform.file.constant.MessageConstant;
import com.csplatform.file.entities.FileInfo;
import com.csplatform.file.entities.vo.FileVO;
import com.csplatform.file.enums.FileStatus;
import com.csplatform.file.enums.UploadStatus;
import com.csplatform.file.mapper.FileMapper;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
    private ThreadPoolTaskExecutor executor;

    @Resource
    private RedisUtil redisUtil;
    @Override
    public void uploadSmallFile(MultipartFile file, FileInfo fileInfo) {

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

                map.put("status", UploadStatus.UPLOAD_FINISH.getStatus());
                map.put("fileId", insertItem.getFileId());
                return map;
            }
        }

        // 检查Redis中是否有这个MD5的键
        Object redisValue = redisUtil.get(fileVO.getFileMd5());
        if (redisValue == null) {
            // 如果Redis中没有，可能是第一次上传但chunkNumber != 1，或者Redis键过期
            // 重新设置并继续上传
            redisUtil.set(fileVO.getFileMd5(), 1);
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

            // 删除redis中的键
            redisUtil.del(fileVO.getFileMd5());
            map.put("status", UploadStatus.UPLOAD_FINISH.getStatus());
            map.put("fileId", insertItem.getFileId());

            return map;
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

            // 删除Redis键
            redisUtil.del(fileVO.getFileMd5());

            map.put("status", UploadStatus.UPLOAD_FINISH.getStatus());
            map.put("fileId", insertItem.getFileId());

            return map;
        }

        // 更新Redis中的上传进度
        redisUtil.incrby(fileVO.getFileMd5(), 1);
        map.put("status", UploadStatus.UPLOADING.getStatus());

        return map;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FolderVO getRootFolderByUserId(Long id) {
        //TODO:先查询是否有这个用户

        //查询root
        LambdaQueryWrapper<FileInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.
                eq(FileInfo::getUserId,id)
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

            int insert = fileInfoMapper.insert(fileInfo);
            if(insert < 0)
                throw  new BusinessException("初始化错误！");
        }

        //拿到根目录ID
        LambdaQueryWrapper<FileInfo> queryWrapper1 = new LambdaQueryWrapper<>();

        queryWrapper1.eq(FileInfo::getFilePid,fileInfo.getFileId()).orderBy(true,true,FileInfo::getCreateTime);

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
        fileInfoLambdaQueryWrapper.eq(FileInfo::getFilePid,id).orderBy(true,true,FileInfo::getCreateTime);

        //查询出所有内容
        List<FileInfo> fileInfos = fileInfoMapper.selectList(fileInfoLambdaQueryWrapper);

        //返回对象
        return fileInfos;
    }
}
