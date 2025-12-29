package com.csplatform.user.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author WangXing
 * @Date 2025/12/29 10:23
 * @PackageName:com.csplatform.user.api
 * @ClassName: ChatService
 * @Version 1.0
 */
@FeignClient(name = "chatService" ,url = "${feign.client.config.service.chatService}",path = "${feign.client.config.path.chatPath}")
public interface ChatService {
    @GetMapping("/test/a")
    public String test();
}
