package com.csplatform.user.controller;

import com.csplatform.user.api.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author WangXing
 * @Date 2025/12/29 9:45
 * @PackageName:com.csplatform.user.controller
 * @ClassName: TestController
 * @Version 1.0
 */
@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/a")
    public String test(){
        log.info("======================================");
        System.out.println(chatService.test());
        return "user";
    }
}
