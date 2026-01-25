package com.csplatform.file.controller;

import com.csplatform.common.exception.BusinessException;
import com.csplatform.common.resp.Result;
import com.csplatform.file.entities.FileInfo;
import com.csplatform.file.entities.vo.FolderVO;
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
        try{
            FileInfo targetFile = fileService.getFileInfoByFileId(id);
            return Result.success(targetFile);
        }catch (BusinessException e){
            return Result.fail(e.getMessage());
        }catch (Exception e){
            return Result.fail("服务器异常！");
        }

    }

    /**
     * 获得当前用户的根目录
     */
    @GetMapping("/root")
    public Result<FolderVO> getCurrentUserRootFolder(@RequestParam("userId") Long id){
        log.info("当前用户 {}",id);
        FolderVO rootFiles = fileService.getRootFolderByUserId(id);
        return Result.success(rootFiles);
    }

    /**
     * 内部：API，获得文件信息
     */
//    @PostMapping("/getFileInfo")
//    public Result<FileInfo> getFileInfoAPI(@RequestBody FileInfo fileInfo){
//        log.info("API file : {}",fileInfo);
//        FileInfo fi = fileService.getFileInfoByFileAPI(fileInfo);
//        return Result.success(fi);
//    }


    /**
     * 通过目录Id获得对应目录信息
     */
    @GetMapping("/folder/{id}")
    public Result<List<FileInfo>> getFolderInfoById(@PathVariable("id") String id){
        log.info("当前文件夹 {}",id);
        List<FileInfo> fileInfos = fileService.getFolderById(id);
        return Result.success(fileInfos);
    }

    /**
     * 新建文件夹
     */
    @PostMapping("/c_folder")
    public Result<String> newFolder(@RequestBody FileInfo folder){
        log.info("创建新的文件夹{}",folder);
        fileService.newFolder(folder);
        return Result.success("新建文件夹成功");
    }

    /**
     * 初始化用户文件信息
     */
    @GetMapping("/init_file_root")
    public Result<String> initFileRoot(@RequestParam("id") Long id){
        log.info("初始化用户根目录");
        fileService.initFileRoot(id);
        return Result.success("新建文件夹成功");
    }

    /**
     * 回收单个
     */
    @GetMapping("/recycle_file/{id}")
    public Result<String> recycleFile(@PathVariable("id")String id){
        //回收文件或文件夹
        log.info("回收ID:{}",id);
        fileService.recycleFile(id);
        return Result.success("OK");
    }

    /**
     * 回收多个文件
     */
    @PostMapping("/recycle_files")
    public Result<String> recycleFiles(@RequestBody List<String> ids){
        //回收文件s
        log.info("文件IDS:{}",ids);
        fileService.recycleFiles(ids);
        return Result.success("OK");
    }


    /**
     * 获得回收站里的文件
     */
    @GetMapping("/get_recycle_files")
    public Result<List<FileInfo>> getRecycleFiles(@RequestParam("userId") Long userId){
        log.info("获得用户：{}的回收站",userId);
        List<FileInfo> files =  fileService.getRecycleFiles(userId);
        return Result.success(files);
    }


    /**
     * 通过folderID获得回收文件
     */
    @GetMapping("/get_recycle_folder")
    public Result<List<FileInfo>> getRecycleFilesByFolderId(@RequestParam("id") String id){
        log.info("回收文件夹id:{}",id);
        List<FileInfo> folder = fileService.getRecycleFolder(id);
        return Result.success(folder);
    }


    /**
     * 彻底删除文件
     */
    @PostMapping("/delete_file")
    public Result<String> deleteFile(@RequestBody List<String> ids){
        log.info("删除的文件：{}",ids);
        fileService.deleteFilesBatch(ids);
        return Result.success("OK");
    }

    /**
     * 复原
     */
    @PostMapping("/restore_file")
    public Result<String> restoreFile(@RequestBody List<String> ids){
        log.info("复原文件：{}",ids);
        fileService.restoreFiles(ids);
        return Result.success("OK");
    }

    @GetMapping("/get_url")
    public Result<String> getUrlByFileId(@RequestParam("fileId") String fileId){
        log.info("fileID: {}",fileId);
        return Result.success(fileService.getFileUrlByFileId(fileId));
    }


}
