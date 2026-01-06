package com.csplatform.file.controller;

import com.csplatform.common.resp.Result;
import com.csplatform.file.entities.FileInfo;
import com.csplatform.file.entities.vo.FileVO;
import com.csplatform.file.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @Author WangXing
 * @Date 2026/1/3 17:21
 * @PackageName:com.csplatform.file.controller
 * @ClassName: FileInfoController
 * @Version 1.0
 */
@RestController
@Slf4j
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private FileService fileService;

    /**
     * 上传多个文件
     */
    @PostMapping("/files")
    public Result<HashMap> uploadFiles(@RequestBody List<FileInfo> files){
        return null;
    }


    /**
     * 上传文件
     * @param file
     * @param chunkNumber
     * @param chunkTotal
     * @param fileName
     * @param fileMd5
     * @param userId
     * @param filePid
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @PostMapping("/one")
    public Result<Map> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("chunkNumber") Integer chunkNumber,
            @RequestParam("chunkTotal") Integer chunkTotal,
            @RequestParam("fileName") String fileName,
            @RequestParam("fileMd5") String fileMd5,
            @RequestParam("userId") Long userId,
            @RequestParam("filePid") String filePid,
            @RequestParam("totalSize") Long totalSize
    ) throws ExecutionException, InterruptedException {

        // 创建FileVO对象
        FileVO fileInfo = new FileVO();
        fileInfo.setFile(file);
        fileInfo.setChunkNumber(chunkNumber);
        fileInfo.setChunkTotal(chunkTotal);
        fileInfo.setFileName(fileName);
        fileInfo.setFileMd5(fileMd5);
        fileInfo.setUserId(userId);
        fileInfo.setFilePid(filePid);
        fileInfo.setTotalSize(totalSize);

        log.info("分片上传文件 {}", fileMd5);
        return Result.success(fileService.uploadFile(fileInfo));
    }

}
