package com.csplatform.user.controller;

import cn.hutool.log.Log;
import com.csplatform.common.exception.BusinessException;
import com.csplatform.common.resp.Result;
import com.csplatform.user.entities.User;
import com.csplatform.user.entities.vo.UserInfoVO;
import com.csplatform.user.mapper.UserMapper;
import com.csplatform.user.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @Author WangXing
 * @Date 2026/1/1 9:32
 * @PackageName:com.csplatform.user.controller
 * @ClassName: UserInfoController
 * @Version 1.0
 */
@RestController
@RequestMapping("/info")
@Slf4j
public class UserInfoController {

    @Autowired
    private UserService userService;

    /**
     * 获得用户信息
     */
    @GetMapping("/{userId}")
    public Result<UserInfoVO> getUserInfo(@PathVariable Long userId){
        log.info("用户ID:" +userId);
        return Result.success(userService.getUserInfo(userId));
    }

    /**通过邮箱获得用户信息
     *
     */
    @GetMapping("/e")  // 路径是 /e/{email}
    public Result<UserInfoVO> getUserInfoByEmail(@RequestParam("email") String email){
        log.info("用户邮箱:" +email);
        return Result.success(userService.getUserInfoByEmail(email));
    }

    /**
     * 修改用户信息
     * @param user
     * @return
     */
    @PostMapping("/modify")
    public Result<String> modifyUserInfo(@RequestBody UserInfoVO user){
        log.info("修改用户信息{}",user);
        userService.modifyUserInfo(user);
        return Result.success("修改成功");
    }

    /**
     * 更新用户文件空间
     * @param userId
     * @param size
     * @return
     */
    @GetMapping("/updateFileSpace")
    Result<String> updateUserFileSpace(@RequestParam("userId") Long userId,@RequestParam("size") Long size){
        log.info("更新用户空间");
        userService.updateUserFileSpace(userId,size);
        return Result.success("OK");
    }

    /**
     * 获得用户空间使用情况
     */
    @GetMapping("/getAllAndUsedSpace")
    public Result<Map<String,Long>> getAllAndUsedSpace(@RequestParam("userId")Long userId){
        log.info("获得用户网盘空间,用户:{}",userId);
        Map<String, Long> result = userService.getUserFileSpace(userId);
        return Result.success(result);
    }


    /**
     * 修改用户头像
     */
    @PostMapping("/modify_avatar")
    public Result<String> modifyUserAvatar(@RequestParam("id") Long id, @RequestParam("file")MultipartFile file){
        log.info("修改用户：{} 的头像",id);
        userService.updateUserAvatar(id,file);
        return Result.success("OK");
    }

}


