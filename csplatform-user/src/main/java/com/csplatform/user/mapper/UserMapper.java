// UserMapper.java
package com.csplatform.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csplatform.user.entities.User;
import com.csplatform.user.entities.vo.SearchUserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    // 根据邮箱查询用户（忽略删除状态）
    User selectByEmailIgnoreDeleted(@Param("email") String email);

    // 更新用户基础信息（动态更新非空字段）
    int updateBasicInfo(@Param("user") User user);

    // 更新用户学习统计信息
    int updateLearningStats(@Param("userId") Long userId,
                            @Param("hours") Integer hours,
                            @Param("completed") Integer completed,
                            @Param("ongoing") Integer ongoing,
                            @Param("certificates") Integer certificates,
                            @Param("exp") Integer exp,
                            @Param("level") Integer level);

    // 更新用户登录信息
    int updateLoginInfo(@Param("userId") Long userId,
                        @Param("ip") String ip);

    // 记录登录失败
    int recordLoginFailure(@Param("email") String email,
                           @Param("lockUntil") Date lockUntil);

    // 更新用户密码
    int updatePassword(@Param("userId") Long userId,
                       @Param("passwordHash") String passwordHash);

    // 验证邮箱
    int verifyEmail(@Param("userId") Long userId);

    // 验证手机
    int verifyPhone(@Param("userId") Long userId);

    // 更新头像
    int updateAvatar(@Param("userId") Long userId,
                     @Param("avatarUrl") String avatarUrl);

    // 软删除用户
    int softDelete(@Param("userId") Long userId);

    // 恢复软删除的用户
    int recoverDeleted(@Param("userId") Long userId);

    // 更新用户通知设置
    int updateNotificationSettings(@Param("userId") Long userId,
                                   @Param("emailNotifications") Integer emailNotifications,
                                   @Param("smsNotifications") Integer smsNotifications,
                                   @Param("pushNotifications") Integer pushNotifications,
                                   @Param("courseUpdatesNotify") Integer courseUpdatesNotify,
                                   @Param("assignmentReminders") Integer assignmentReminders,
                                   @Param("newsletterSubscribed") Integer newsletterSubscribed);

    // 根据条件查询用户（分页）
    List<User> selectUsersByCondition(@Param("condition") Map<String, Object> condition);

    // 统计用户数据
    Map<String, Object> selectUserStatistics();

    // 批量更新用户状态
    int batchUpdateAccountStatus(@Param("userIds") List<Long> userIds,
                                 @Param("status") Integer status);

    // 获取用户活跃度统计
    List<Map<String, Object>> selectUserActivityStats();

    //获得空间使用情况
    Map<String, Long> getSizeInfo(@Param("id") Long id);

    SearchUserVO searchAddUserInfo(@Param("id") Long id);
}