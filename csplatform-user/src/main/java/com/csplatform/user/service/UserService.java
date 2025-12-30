package com.csplatform.user.service;



import com.baomidou.mybatisplus.extension.service.IService;
import com.csplatform.user.entities.User;
import com.csplatform.user.entities.dto.LoginDTO;
import com.csplatform.user.entities.vo.LoginResultVO;

/**
 * 简化的用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录
     */
    LoginResultVO login(LoginDTO loginDTO);

    /**
     * 用户登出
     */
    boolean logout(String token);

    /**
     * 获取当前登录用户信息
     */
    User getCurrentUser(String token);
}