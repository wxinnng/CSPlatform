package com.csplatform.user.controller;

/**
 * @Author WangXing
 * @Date 2025/12/30 16:39
 * @PackageName:com.csplatform.user.controller
 * @ClassName: UserController
 * @Version 1.0
 */

import com.csplatform.common.exception.BusinessException;
import com.csplatform.common.resp.Result;
import com.csplatform.user.entities.User;
import com.csplatform.user.entities.dto.LoginDTO;
import com.csplatform.user.entities.dto.RegisterDTO;
import com.csplatform.user.entities.vo.LoginResultVO;
import com.csplatform.user.entities.vo.RegisterResultVO;
import com.csplatform.user.service.UserService;

import com.csplatform.user.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Validated
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<LoginResultVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginResultVO loginResult = userService.login(loginDTO);
        log.info("用户登录成功: {}", loginDTO.getEmail());
        return Result.success("登录成功", loginResult);

    }

    @PostMapping("/logout")
    public Result<String> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            userService.logout(token);
        }
        return Result.success("登出成功");
    }

    @GetMapping("/current-user")
    public Result<LoginResultVO.UserVO> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.fail("未登录");
        }

        token = token.substring(7);
        User user = userService.getCurrentUser(token);

        if (user == null) {
            return Result.fail("用户未登录或会话已过期");
        }

        return Result.success(LoginResultVO.UserVO.fromUser(user));
    }

    @PostMapping("/refresh-token")
    public Result<LoginResultVO> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null) {
            return Result.fail("刷新令牌不能为空");
        }

        LoginResultVO result = ((UserServiceImpl) userService).refreshToken(refreshToken);
        return Result.success("令牌刷新成功", result);
    }

    @PostMapping("/register")
    public Result<RegisterResultVO> register(/*@Valid */@RequestBody RegisterDTO registerDTO){
        //注册操作
        RegisterResultVO registerResultVO = userService.register(registerDTO);
        //返回结果
        return Result.success("注册成功",registerResultVO);

    }

}