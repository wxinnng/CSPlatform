package com.csplatform.user.service;



import com.baomidou.mybatisplus.extension.service.IService;
import com.csplatform.user.entities.User;
import com.csplatform.user.entities.dto.LoginDTO;
import com.csplatform.user.entities.dto.RegisterDTO;
import com.csplatform.user.entities.vo.LoginResultVO;
import com.csplatform.user.entities.vo.RegisterResultVO;
import com.csplatform.user.entities.vo.SearchUserVO;
import com.csplatform.user.entities.vo.UserInfoVO;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

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

    /**
     * 用户注册
     * @param registerDTO
     * @return
     */
    RegisterResultVO register(@Valid RegisterDTO registerDTO);


    /**
     * 获取用户信息
     */
    UserInfoVO getUserInfo(Long userId);

    /**
     * 修改用户信息
     * @param user
     */
    void modifyUserInfo(@Valid UserInfoVO user);

    UserInfoVO getUserInfoByEmail(String email);

    /**
     * 获得用户文件信息
     * @param userId
     * @return
     */
    Map<String, Long> getUserFileSpace(Long userId);

    /**
     * 更新用户文件空间
     * @param userId
     * @param size
     */
    void updateUserFileSpace(Long userId, Long size);

    /**
     * 修改用户头像
     * @param id
     * @param file
     */
    void updateUserAvatar(Long id, MultipartFile file);

    /**
     * 获得用户头像
     * @param id
     * @return
     */
    String getUserAvatar(Long id);

    /**
     * 搜索用户
     * @param id
     * @return
     */
    SearchUserVO searchUserById(Long id);
}