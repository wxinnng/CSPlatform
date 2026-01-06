package com.csplatform.file.api;

import com.csplatform.common.resp.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @Author WangXing
 * @Date 2026/1/5 16:42
 * @PackageName:com.csplatform.file.api
 * @ClassName: UserService
 * @Version 1.0
 */
@FeignClient(name = "userService" ,url = "${feign.client.config.service.userService}",path = "${feign.client.config.path.userPath}")
public interface UserService {

    /**
     * 更新用户网盘占用空间
     */
    @GetMapping("/info/updateFileSpace")
    Result<String> updateUserFileSpace(@RequestParam("userId") Long userId,@RequestParam("size") Long size);


    /**
     * 获得用户文件空间信息
     */
    @GetMapping("/info/getAllAndUsedSpace")
    Result<Map<String,Long>> getAllAndUsedSpace(@RequestParam("userId")Long userId);

}
