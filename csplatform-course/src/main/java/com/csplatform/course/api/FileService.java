package com.csplatform.course.api;

import com.csplatform.common.resp.Result;
import com.csplatform.course.entity.FileInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author WangXing
 * @Date 2026/1/5 15:51
 * @PackageName:com.csplatform.user.api
 * @ClassName: FileService
 * @Version 1.0
 */
@FeignClient(name = "fileService" ,url = "${feign.client.config.service.fileService}",path = "${feign.client.config.path.filePath}")
public interface FileService {

    /**
     * 获得文件信息
     */
    /**
     * 通过id获得对应文件信息
     */
    @GetMapping("/info/getById/{id}")
    public Result<FileInfo> getFileInfoById(@PathVariable("id") String id);
}
