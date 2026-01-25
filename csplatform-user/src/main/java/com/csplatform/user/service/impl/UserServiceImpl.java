package com.csplatform.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csplatform.common.exception.BusinessException;
import com.csplatform.common.resp.Result;
import com.csplatform.common.utils.ChineseTrendyUsernameGenerator;
import com.csplatform.user.api.FileService;
import com.csplatform.user.entities.User;
import com.csplatform.user.entities.dto.LoginDTO;
import com.csplatform.user.entities.dto.RegisterDTO;
import com.csplatform.user.entities.vo.LoginResultVO;
import com.csplatform.user.entities.vo.LoginResultVO.UserVO;
import com.csplatform.user.entities.vo.RegisterResultVO;
import com.csplatform.user.entities.vo.SearchUserVO;
import com.csplatform.user.entities.vo.UserInfoVO;
import com.csplatform.user.mapper.UserMapper;
import com.csplatform.user.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 简化的用户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {

    // 使用@Resource或@Autowired注入
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // 通过构造器注入HttpServletRequest
    private final jakarta.servlet.http.HttpServletRequest request;

    // 配置从application.yml读取
    @Value("${jwt.secret:default-secret-key-change-in-production}")
    private String jwtSecret;

    @Value("${jwt.expire:3600}")
    private Long jwtExpire;

    @Value("${jwt.refresh-expire:604800}")
    private Long refreshTokenExpire;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FileService fileService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResultVO login(LoginDTO loginDTO) {
        // 1. 参数验证
        validateLoginDTO(loginDTO);

        // 2. 获取用户 - 使用MyBatis-Plus的getOne方法
        User user = getUserByEmail(loginDTO.getEmail());

        // 3. 验证账户状态
        validateAccountStatus(user);

        // 4. 验证密码
        validatePassword(user, loginDTO.getPassword());

        // 5. 验证验证码（如果开启）
        validateCaptcha(loginDTO);


        // 7. 生成Token
        String token = generateToken(user, loginDTO.getRememberMe());
        String refreshToken = generateRefreshToken(user);

        // 8. 获取用户角色和权限
        List<String> roles = getUserRoles(user.getId());
        List<String> permissions = getPermissionsByRoles(roles);

        // 9. 返回登录结果
        return buildLoginResult(user, token, refreshToken, roles, permissions, loginDTO.getRememberMe());
    }

    @Override
    public boolean logout(String token) {
        try {
            // 从Redis中移除Token
            String tokenKey = "user:token:" + token;
            String refreshTokenKey = "user:refresh_token:" + token;

            redisTemplate.delete(Arrays.asList(tokenKey, refreshTokenKey));

            // 记录登出日志
            log.info("用户登出成功，token: {}", token);
            return true;
        } catch (Exception e) {
            log.error("用户登出失败", e);
            return false;
        }
    }

    @Override
    public User getCurrentUser(String token) {
        try {
            // 从Redis获取用户ID
            String tokenKey = "user:token:" + token;
            String userIdStr = (String) redisTemplate.opsForValue().get(tokenKey);

            if (StrUtil.isBlank(userIdStr)) {
                return null;
            }

            Long userId = Long.parseLong(userIdStr);
            // 使用MyBatis-Plus的getById方法
            return this.getById(userId);
        } catch (Exception e) {
            log.error("获取当前用户失败", e);
            return null;
        }
    }

    @Override
    public void updateUserFileSpace(Long userId, Long size) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .setSql("used_size = used_size + " + size)
                .eq(User::getId, userId);
        int update = userMapper.update(null, updateWrapper);
        if(update < 1)
            throw new BusinessException("更新失败！");
    }

    @Override
    public SearchUserVO searchUserById(Long id) {
        SearchUserVO searchUserVO = userMapper.searchAddUserInfo(id);
        if(searchUserVO == null)
            throw new BusinessException("没有该用户!");

        return searchUserVO;
    }

    @Override
    public String getUserAvatar(Long id) {
        LambdaQueryWrapper<User> wrap = new LambdaQueryWrapper<>();
        wrap.select(User::getAvatarUrl)
                .eq(User::getId,id);
        //返回头像
        return userMapper.selectOne(wrap).getAvatarUrl();
    }

    @Override
    public void updateUserAvatar(Long id, MultipartFile file) {
        //1.调远程服务，上传头像
        Result<String> result = fileService.uploadSmallFile(file);

        //2.更新用户信息
        if(result.getCode() != 200){
            throw new BusinessException("头像更新失败，请稍后再试！");
        }
        userMapper.updateAvatar(id,result.getData());
    }

    @Override
    public Map<String, Long> getUserFileSpace(Long userId) {
        return userMapper.getSizeInfo(userId);
    }

    @Override
    public UserInfoVO getUserInfoByEmail(String email) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email)
                .eq(User::getDeleted, false); // 软删除过滤

        // 使用getOne方法获取单个用户
        User user = this.getOne(queryWrapper);

        //返回对象
        return new UserInfoVO().setId(user.getId())
                .setPhone(user.getPhone())
                .setAvatarUrl(user.getAvatarUrl())
                .setUsername(user.getUsername())
                .setGender(user.getGender())
                .setRole(user.getRoles())
                .setEmail(user.getEmail())
                .setLearningLevel(user.getLearningLevel())
                .setBackgroundUrl(user.getBackgroundUrl()) // 背景
                .setCreateTime(user.getCreatedAt());

    }

    @Override
    public void modifyUserInfo(UserInfoVO user) {

        User user1 = new User();

        //新建user对象
        user1.setEmail(user.getEmail());
        user1.setUsername(user.getUsername());
        user1.setPasswordHash(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        user1.setGender(user.getGender());
        user1.setPhone(user.getPhone());
        user1.setBirthday(user.getBirthday());
        user1.setId(user.getId());


        int result = userMapper.updateBasicInfo(user1);
        if(result < 1){
            throw new BusinessException("更新失败，请稍后再试！");
        }
    }

    @Override
    public UserInfoVO getUserInfo(Long userId) {
        //查询user对象
        User user = userMapper.selectById(userId);
        //返回对象
        return new UserInfoVO().setId(userId)
                .setPhone(user.getPhone())
                .setAvatarUrl(user.getAvatarUrl())
                .setUsername(user.getUsername())
                .setGender(user.getGender())
                .setRole(user.getRoles())
                .setEmail(user.getEmail())
                .setLearningLevel(user.getLearningLevel())
                .setBackgroundUrl(user.getBackgroundUrl()) // 背景
                .setCreateTime(user.getCreatedAt());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RegisterResultVO register(RegisterDTO registerDTO) {

        //1.验证信息
        if(registerDTO.getEmail() == null || registerDTO.getPassword() == null)
            throw new BusinessException("账号密码不能为空！");

        //2.判断是否已经注册过
        isRegistered(registerDTO.getEmail());

        //1. 插入用户信息
        //1.1 生成加密后的密码
        String encryptedPassword = DigestUtils.md5DigestAsHex(registerDTO.getPassword().getBytes());

        //1.2 创建用户对象
        //时间
        LocalDateTime now = LocalDateTime.now();
        User targetUser = new User()
                .setEmail(registerDTO.getEmail())
                .setPasswordHash(encryptedPassword)
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .setUsername(ChineseTrendyUsernameGenerator.generateTrendyUsername())
                .setTotalSize(10d)
                .setTotalSize(0d);
        //1.3 插入用户信息
        int insert = userMapper.insert(targetUser);

        if(insert < 1)
            throw new BusinessException("服务器异常，请稍后再试！");


        //1.4远程调用
        Result<String> result = fileService.initFileRoot(targetUser.getId());
        if(result.getCode() != 200)
            throw new BusinessException("用户初始化错误！");

        //2. 返回对象
        return new RegisterResultVO().setId(targetUser.getId()).setEmail(targetUser.getEmail());

    }

    /**
     * 检测邮箱是否已经被注册过
     * @param email
     */
    private void isRegistered(@NotBlank(message = "邮箱不能为空") @Email(message = "邮箱格式不正确") String email) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email)
                .eq(User::getDeleted, false); // 软删除过滤

        // 使用getOne方法获取单个用户
        User user = this.getOne(queryWrapper);

        if (user != null)
            throw new BusinessException("该邮箱已被注册，请直接登录。");
    }


    /**
     * 验证登录参数
     */
    private void validateLoginDTO(LoginDTO loginDTO) {
        if (loginDTO == null) {
            throw new BusinessException("登录参数不能为空");
        }

        if (StrUtil.isBlank(loginDTO.getEmail())) {
            throw new BusinessException("邮箱不能为空");
        }

        if (StrUtil.isBlank(loginDTO.getPassword())) {
            throw new BusinessException("密码不能为空");
        }
    }

    /**
     * 根据邮箱获取用户 - 使用MyBatis-Plus的getOne方法
     */
    private User getUserByEmail(String email) {
        // 使用LambdaQueryWrapper构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email)
                .eq(User::getDeleted, false); // 软删除过滤

        // 使用getOne方法获取单个用户
        User user = this.getOne(queryWrapper);

        if (user == null) {
            throw new BusinessException("邮箱或密码错误");
        }

        return user;
    }

    /**
     * 验证账户状态
     */
    private void validateAccountStatus(User user) {
        // 检查账户状态
        if (user.getAccountStatus() == User.AccountStatus.DISABLED.getCode()) {
            throw new BusinessException("账户已被禁用");
        }

        if (user.getAccountStatus() == User.AccountStatus.LOCKED.getCode()) {
            throw new BusinessException("账户已被锁定，请联系管理员");
        }

        if (user.getAccountStatus() == User.AccountStatus.INACTIVE.getCode()) {
            throw new BusinessException("账户未激活，请先验证邮箱");
        }

    }

    /**
     * 更新账户锁定状态 - 使用MyBatis-Plus的update方法
     */
    private boolean updateAccountLockStatus(Long userId, Integer status, Integer lockMinutes) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(User::getAccountStatus, status)
                .eq(User::getId, userId);

        return this.update(updateWrapper);
    }

    /**
     * 验证密码
     */
    private void validatePassword(User user, String password) {
        // 使用Spring的DigestUtils进行MD5加密
        String encryptedPassword = DigestUtils.md5DigestAsHex(password.getBytes());

        System.out.println(encryptedPassword);
    }


    /**
     * 增加登录失败次数 - 使用MyBatis-Plus的update方法
     */
    private boolean incrementFailedLoginAttempts(Long userId) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.setSql("failed_login_attempts = failed_login_attempts + 1")
                .eq(User::getId, userId);

        return this.update(updateWrapper);
    }

    /**
     * 重置登录失败次数 - 使用MyBatis-Plus的update方法
     */
    private boolean resetFailedLoginAttempts(Long userId) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .eq(User::getId, userId);

        return this.update(updateWrapper);
    }

    /**
     * 验证验证码
     */
    private void validateCaptcha(LoginDTO loginDTO) {
        if (StrUtil.isNotBlank(loginDTO.getCaptcha()) &&
                StrUtil.isNotBlank(loginDTO.getCaptchaKey())) {

            String redisKey = "captcha:" + loginDTO.getCaptchaKey();
            String correctCaptcha = (String) redisTemplate.opsForValue().get(redisKey);

            if (StrUtil.isBlank(correctCaptcha)) {
                throw new BusinessException("验证码已过期");
            }

            if (!loginDTO.getCaptcha().equalsIgnoreCase(correctCaptcha)) {
                throw new BusinessException("验证码错误");
            }

            // 验证成功后删除验证码
            redisTemplate.delete(redisKey);
        }
    }


    /**
     * 生成访问令牌
     */
    private String generateToken(User user, Boolean rememberMe) {
        String token = UUID.randomUUID().toString().replace("-", "");

        // 存储到Redis
        String tokenKey = "user:token:" + token;
        long expireTime = Boolean.TRUE.equals(rememberMe) ? refreshTokenExpire : jwtExpire;

        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("userId", user.getId());
        tokenData.put("username", user.getUsername());
        tokenData.put("email", user.getEmail());
        tokenData.put("loginTime", LocalDateTime.now().toString());

        redisTemplate.opsForValue().set(tokenKey, user.getId().toString(), expireTime, TimeUnit.SECONDS);
        redisTemplate.opsForHash().putAll("user:token:data:" + token, tokenData);
        redisTemplate.expire("user:token:data:" + token, expireTime, TimeUnit.SECONDS);

        return token;
    }

    /**
     * 生成刷新令牌
     */
    private String generateRefreshToken(User user) {
        String refreshToken = UUID.randomUUID().toString().replace("-", "");

        String refreshTokenKey = "user:refresh_token:" + refreshToken;
        redisTemplate.opsForValue().set(refreshTokenKey, user.getId().toString(),
                refreshTokenExpire, TimeUnit.SECONDS);

        return refreshToken;
    }

    /**
     * 获取用户角色
     */
    private List<String> getUserRoles(Long userId) {
        List<String> roles = new ArrayList<>();
        roles.add("STUDENT");

        // 使用MyBatis-Plus的getById方法获取用户
        User user = this.getById(userId);
        if (user != null && user.getLevel() >= 10) {
            roles.add("VIP");
        }

        return roles;
    }

    /**
     * 根据角色获取权限
     */
    private List<String> getPermissionsByRoles(List<String> roles) {
        List<String> permissions = new ArrayList<>();
        permissions.add("course:view");
        permissions.add("course:enroll");

        if (roles.contains("VIP")) {
            permissions.add("course:download");
            permissions.add("course:premium");
        }

        return permissions;
    }

    /**
     * 构建登录结果
     */
    private LoginResultVO buildLoginResult(User user, String token, String refreshToken,
                                           List<String> roles, List<String> permissions, Boolean rememberMe) {
        LoginResultVO result = new LoginResultVO();
        result.setToken(token);
        result.setRefreshToken(refreshToken);
        result.setTokenType("Bearer");
        result.setExpiresIn(Boolean.TRUE.equals(rememberMe) ? refreshTokenExpire : jwtExpire);
        result.setUser(UserVO.fromUser(user));
        result.setRoles(roles);
        result.setPermissions(permissions);

        return result;
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp() {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip)) {
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }

        ip = request.getHeader("X-Real-IP");
        if (ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        return request.getRemoteAddr();
    }

    /**
     * 记录登录日志
     */
    private void logLogin(Long userId, String ip, boolean success) {
        log.info("用户登录: userId={}, ip={}, success={}, time={}",
                userId, ip, success, LocalDateTime.now());
    }


    public LoginResultVO refreshToken(String refreshToken) {
        String refreshTokenKey = "user:refresh_token:" + refreshToken;
        String userIdStr = (String) redisTemplate.opsForValue().get(refreshTokenKey);

        if (StrUtil.isBlank(userIdStr)) {
            throw new BusinessException("刷新令牌无效或已过期");
        }

        // 删除旧的刷新令牌
        redisTemplate.delete(refreshTokenKey);

        Long userId = Long.parseLong(userIdStr);
        // 使用MyBatis-Plus的getById方法获取用户
        User user = this.getById(userId);

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 生成新的令牌
        String newToken = generateToken(user, false);
        String newRefreshToken = generateRefreshToken(user);

        List<String> roles = getUserRoles(user.getId());
        List<String> permissions = getPermissionsByRoles(roles);

        return buildLoginResult(user, newToken, newRefreshToken, roles, permissions, false);
    }

    /**
     * 根据用户名或邮箱查询用户 - 示例方法
     */
    public User getUserByUsernameOrEmail(String account) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper
                        .eq(User::getUsername, account)
                        .or()
                        .eq(User::getEmail, account))
                .eq(User::getDeleted, false);

        return this.getOne(queryWrapper);
    }

    /**
     * 更新用户头像 - 示例方法
     */
    public boolean updateUserAvatar(Long userId, String avatarUrl) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(User::getAvatarUrl, avatarUrl)
                .eq(User::getId, userId);

        return this.update(updateWrapper);
    }

    /**
     * 分页查询用户列表 - 示例方法
     */
    public List<User> getUsersByCondition(String keyword, Integer status, Integer minLevel) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        // 条件判断
        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(User::getUsername, keyword)
                    .or()
                    .like(User::getEmail, keyword));
        }

        if (status != null) {
            queryWrapper.eq(User::getAccountStatus, status);
        }

        if (minLevel != null) {
            queryWrapper.ge(User::getLevel, minLevel);
        }

        queryWrapper.eq(User::getDeleted, false)
                .orderByDesc(User::getCreatedAt);

        return this.list(queryWrapper);
    }

    /**
     * 统计用户数量 - 示例方法
     */
    public Map<String, Long> countUserStats() {
        Map<String, Long> stats = new HashMap<>();

        // 总用户数
        LambdaQueryWrapper<User> totalWrapper = new LambdaQueryWrapper<>();
        totalWrapper.eq(User::getDeleted, false);
        stats.put("total", this.count(totalWrapper));

        // 活跃用户数（今天登录过的）
        LambdaQueryWrapper<User> activeWrapper = new LambdaQueryWrapper<>();
        activeWrapper.ge(User::getLastLoginTime, LocalDateTime.now().minusDays(1))
                .eq(User::getDeleted, false);
        stats.put("active", this.count(activeWrapper));

        // VIP用户数
        LambdaQueryWrapper<User> vipWrapper = new LambdaQueryWrapper<>();
        vipWrapper.ge(User::getLevel, 10)
                .eq(User::getDeleted, false);
        stats.put("vip", this.count(vipWrapper));

        return stats;
    }

}