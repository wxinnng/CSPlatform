package com.csplatform.user.controller;

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
        try{
            return Result.success(userService.getUserInfo(userId));
        }catch (BusinessException e){
            return Result.fail(e.getMessage());
        }catch (Exception e){
            return Result.fail("服务器异常，请稍后再试！");
        }
    }

    /**通过邮箱获得用户信息
     *
     */
    @GetMapping("/e")  // 路径是 /e/{email}
    public Result<UserInfoVO> getUserInfoByEmail(@RequestParam("email") String email){
        log.info("用户邮箱:" +email);
        try{
            return Result.success(userService.getUserInfoByEmail(email));
        }catch (BusinessException e){
            return Result.fail(e.getMessage());
        }catch (Exception e){
            return Result.fail("服务器异常，请稍后再试！");
        }
    }

    /**
     * 修改用户信息
     * @param user
     * @return
     */
    @PostMapping("/modify")
    public Result<String> modifyUserInfo(@RequestBody UserInfoVO user){
        try{
            userService.modifyUserInfo(user);
            return Result.success("修改成功");
        }catch (BusinessException e){
            return Result.fail(e.getMessage());
        }catch (Exception e){
            return Result.fail("服务器异常，请稍后再试！");
        }
    }

}
