package com.csplatform.file.controller;

import com.csplatform.file.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author WangXing
 * @Date 2025/12/28 12:59
 * @PackageName:com.csplatform.file.controller
 * @ClassName: UserController
 * @Version 1.0
 */
@RestController
@RequestMapping("/test")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/a")
    public String User(){
        return "wangxing";
    }
}
