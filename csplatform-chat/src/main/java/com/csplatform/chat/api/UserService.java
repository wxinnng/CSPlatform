package com.csplatform.chat.api;

import com.csplatform.chat.entities.vo.SearchUserVO;
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
     * 搜索用户
     */
    @GetMapping("/info/search_user")
    Result<SearchUserVO> searchUser(@RequestParam("id") Long id);
}
