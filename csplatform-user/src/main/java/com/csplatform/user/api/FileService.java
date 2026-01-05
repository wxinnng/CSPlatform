package com.csplatform.user.api;

import com.csplatform.common.resp.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    @GetMapping("/info/init_file_root")
    public Result<String> initFileRoot(@RequestParam("id") Long id); // 正确：显式指定参数名为"id"
}
