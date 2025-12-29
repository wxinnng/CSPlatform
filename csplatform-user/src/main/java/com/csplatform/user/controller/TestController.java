package com.csplatform.user.controller;

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
public class TestController {
    @GetMapping("/a")
    public String test(){
        return "user";
    }
}
