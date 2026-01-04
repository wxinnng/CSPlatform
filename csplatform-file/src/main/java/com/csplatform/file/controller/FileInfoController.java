package com.csplatform.file.controller;

import com.csplatform.common.resp.Result;
import com.csplatform.file.entities.FileInfo;
import com.csplatform.file.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.simpleframework.xml.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author WangXing
 * @Date 2026/1/4 15:21
 * @PackageName:com.csplatform.file.controller
 * @ClassName: FileInfoController
 * @Version 1.0
 */
@RestController
@Slf4j
@RequestMapping("/info")
public class FileInfoController {


    @Autowired
    private FileService fileService;

    /**
     * 通过id获得对应文件信息
     */
    @GetMapping("/getById/{id}")
    public Result<FileInfo> getFileInfoById(@PathVariable("id") String id){
        log.info("文件ID {}",id);
        FileInfo targetFile = fileService.getFileInfoByFileId(id);
        return Result.success(targetFile);
    }

    /**
     * 获得当前用户的根目录
     */
    @GetMapping("/root")
    public Result<List<FileInfo>> getCurrentUserRootFolder(@RequestParam("userId") Long id){
        log.info("当前用户 {}",id);
        List<FileInfo> rootFiles = fileService.getRootFolderByUserId(id);
        return Result.success(rootFiles);
    }


    /**
     * 通过目录Id获得对应目录信息
     */
    @GetMapping("/folder/{id}")
    public Result<List<FileInfo>> getFolderInfoById(@PathVariable("id") String id){
        log.info("当前文件夹 {}",id);
        List<FileInfo> fileInfos = fileService.getFolderById(id);
        return Result.success(fileInfos);
    }

}
