package com.csplatform.user.entities.vo;

import com.csplatform.user.entities.User;
import lombok.Data;
import lombok.experimental.Accessors;
import java.util.List;

/**
 * 用户登录成功后的响应结果
 *
 * @author WangXing
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class LoginResultVO {

    /**
     * 用于访问接口的认证令牌
     */
    private String token;

    /**
     * 用于刷新访问令牌的令牌
     */
    private String refreshToken;

    /**
     * 令牌类型，例如 'Bearer'
     */
    private String tokenType = "Bearer";

    /**
     * 访问令牌的剩余过期时间（单位：秒）
     */
    private Long expiresIn = 3600L;

    /**
     * 登录用户的基本信息
     */
    private UserVO user;

    /**
     * 用户当前所拥有的权限码列表
     */
    private List<String> permissions;

    /**
     * 用户当前所属的角色码列表
     */
    private List<String> roles;

    /**
     * 用户信息视图对象（简化版）
     */
    @Data
    @Accessors(chain = true)
    public static class UserVO {
        /**
         * 用户唯一标识ID
         */
        private Long id;

        /**
         * 用户名/账号
         */
        private String username;

        /**
         * 邮箱地址
         */
        private String email;

        /**
         * 用户昵称
         */
        private String nickname;

        /**
         * 用户真实姓名
         */
        private String realName;

        /**
         * 用户头像的URL地址
         */
        private String avatarUrl;

        /**
         * 手机号码
         */
        private String phone;

        /**
         * 账户状态编码 (例如: 0-正常, 1-禁用)
         */
        private Integer accountStatus;

        /**
         * 账户状态描述信息
         */
        private String accountStatusDesc;

        /**
         * 用户等级
         */
        private Integer level;

        /**
         * 用户经验值
         */
        private Integer experiencePoints;

        /**
         * 总学习时长（单位：小时）
         */
        private Integer totalLearningHours;

        /**
         * 已完成的课程数量
         */
        private Integer completedCourses;

        /**
         * 从 User 实体对象转换为 UserVO 对象
         *
         * @param user 用户实体
         * @return 转换后的用户视图对象，若输入为null则返回null
         */
        public static UserVO fromUser(User user) {
            if (user == null) {
                return null;
            }

            return new UserVO()
                    .setId(user.getId())
                    .setUsername(user.getUsername())
                    .setEmail(user.getEmail())
                    .setAvatarUrl(user.getAvatarUrl())
                    .setPhone(user.getPhone())
                    .setAccountStatus(user.getAccountStatus())
                    .setAccountStatusDesc(user.getAccountStatusDesc())
                    .setLevel(user.getLevel())
                    .setTotalLearningHours(user.getTotalLearningHours())
                    .setCompletedCourses(user.getCompletedCourses());
        }
    }
}