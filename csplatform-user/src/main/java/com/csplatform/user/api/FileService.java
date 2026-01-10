package com.csplatform.user.api;

import com.csplatform.common.resp.Result;
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
     * 初始化root
     */
    @PostMapping("/info/init_file_root")
    public Result<String> initFileRoot(@RequestParam("id") Long id); // 正确：显式指定参数名为"id"


    /**
     * 上传头像
     */
    @PostMapping(value = "/upload/upload_small_file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<String> uploadSmallFile(@RequestPart("file") MultipartFile file);
}
