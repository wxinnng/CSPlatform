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
        try{
            FolderVO rootFiles = fileService.getRootFolderByUserId(id);
            return Result.success(rootFiles);
        }catch (BusinessException e){
            return Result.fail(e.getMessage());
        }catch (Exception e){
            return Result.fail("服务器异常！");
        }

    }


    /**
     * 通过目录Id获得对应目录信息
     */
    @GetMapping("/folder/{id}")
    public Result<List<FileInfo>> getFolderInfoById(@PathVariable("id") String id){
        log.info("当前文件夹 {}",id);
        try{
            List<FileInfo> fileInfos = fileService.getFolderById(id);
            return Result.success(fileInfos);
        }catch (BusinessException e){
            return Result.fail(e.getMessage());
        }catch (Exception e){
            return Result.fail("服务器异常！");
        }

    }

    /**
     * 新建文件夹
     */
    @PostMapping("/c_folder")
    public Result<String> newFolder(@RequestBody FileInfo folder){
        log.info("创建新的文件夹{}",folder);
        try{
            fileService.newFolder(folder);
            return Result.success("新建文件夹成功");
        }catch (BusinessException e){
            return Result.fail(e.getMessage());
        }catch (Exception e){
            return Result.fail("服务器异常！");
        }
    }

    /**
     * 初始化用户文件信息
     */
    @GetMapping("/init_file_root")
    public Result<String> initFileRoot(@RequestParam("id") Long id){
        log.info("初始化用户根目录");
//        try{
            fileService.initFileRoot(id);
            return Result.success("新建文件夹成功");
//        }catch (BusinessException e){
//            return Result.fail(e.getMessage());
//        }catch (Exception e){
//            return Result.fail("服务器异常！");
//        }
    }
}
