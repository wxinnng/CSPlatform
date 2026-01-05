package com.csplatform.file.service;

import com.csplatform.file.entities.FileInfo;
import com.csplatform.file.entities.vo.FileVO;
import com.csplatform.file.entities.vo.FolderVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @Author WangXing
 * @Date 2026/1/3 17:23
 * @PackageName:com.csplatform.file.service
 * @ClassName: UploadService
 * @Version 1.0
 */

public interface FileService {

    /**
     * 上传小文件（不用分配）
     */
    void uploadSmallFile(MultipartFile file, FileInfo fileInfo);

    /**
     * 分片上传
     * @param fileVO
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    HashMap<Object, Object> uploadFile(FileVO fileVO) throws ExecutionException, InterruptedException;

    /**
     * 通过用户ID，获得根目录信息
     * @param id
     * @return
     */
    FolderVO getRootFolderByUserId(Long id);

    /**
     * 通过文件ID获得文件信息
     * @param id
     * @return
     */
    FileInfo getFileInfoByFileId(String id);


    /**
     * 通过fileInfo.id获得对应文件夹里的内容
     * @return
     */
    List<FileInfo> getFolderById(String id);

    /**
     * 创建新的文件夹
     * @param folder
     */
    void newFolder(FileInfo folder);

    /**
     * 初始化文件目录
     * @param id
     */
    void initFileRoot(Long id);
}
