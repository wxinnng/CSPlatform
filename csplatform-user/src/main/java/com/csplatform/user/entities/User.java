package com.csplatform.user.entities;

/**
 * @Author WangXing
 * @Date 2025/12/30 15:36
 * @PackageName:com.csplatform.user.entities
 * @ClassName: User
 * @Version 1.0
 */



import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
public class User extends Model<User> {

    private String backgroundUrl;

    /**
     * 账户状态枚举
     */
    public enum AccountStatus {
        DISABLED(0, "禁用"),
        NORMAL(1, "正常"),
        LOCKED(2, "锁定"),
        INACTIVE(3, "未激活");

        private final int code;
        private final String desc;

        AccountStatus(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        public static AccountStatus fromCode(int code) {
            for (AccountStatus status : values()) {
                if (status.code == code) {
                    return status;
                }
            }
            return NORMAL;
        }
    }

    /**
     * 性别枚举
     */
    public enum Gender {
        UNKNOWN(0, "未知"),
        MALE(1, "男"),
        FEMALE(2, "女");

        private final int code;
        private final String desc;

        Gender(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    /**
     * 学习水平枚举
     */
    public enum LearningLevel {
        BEGINNER("beginner", "初级"),
        INTERMEDIATE("intermediate", "中级"),
        ADVANCED("advanced", "高级");

        private final String code;
        private final String desc;

        LearningLevel(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "用户名只能包含字母、数字、下划线和连字符")
    @TableField("username")
    private String username;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    @TableField("email")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @TableField("phone")
    private String phone;

//    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
    @JsonIgnore
    @TableField("password_hash")
    private String passwordHash;


    @TableField("avatar_url")
    private String avatarUrl;

    @TableField("gender")
    private Integer gender = Gender.UNKNOWN.getCode();

    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("birthday")
    private LocalDate birthday;

    // 学习相关字段
    @TableField("learning_level")
    private String learningLevel;


    @Size(max = 500, message = "个人简介长度不能超过500个字符")
    @TableField("bio")
    private String bio;

    // 账户状态
    @TableField("account_status")
    private Integer accountStatus = AccountStatus.NORMAL.getCode();


    // 安全相关
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    // 学习统计
    @TableField("total_learning_hours")
    private Integer totalLearningHours = 0;

    @TableField("completed_courses")
    private Integer completedCourses = 0;

    @TableField("ongoing_courses")
    private Integer ongoingCourses = 0;

    @TableField("level")
    private Integer level = 1;

    //文件相关
    @TableField("total_size")
    private Double totalSize;

    @TableField("used_size")
    private Double usedSize;




    // 时间戳
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;



    // 软删除
    @TableField("deleted")
    @TableLogic(value = "0", delval = "1")
    private Boolean deleted = false;

    @TableField("deleted_at")
    private LocalDateTime deletedAt;

    // 非数据库字段
    @TableField(exist = false)
    private String password; // 仅用于接收前端传过来的密码

    @TableField(exist = false)
    private List<String> roles; // 用户角色列表

    @TableField(exist = false)
    private Map<String, Object> preferences; // 用户偏好设置



    /**
     * 获取性别描述
     */
    public String getGenderDesc() {
        for (Gender g : Gender.values()) {
            if (g.getCode() == this.gender) {
                return g.getDesc();
            }
        }
        return Gender.UNKNOWN.getDesc();
    }

    /**
     * 获取账户状态描述
     */
    public String getAccountStatusDesc() {
        for (AccountStatus status : AccountStatus.values()) {
            if (status.getCode() == this.accountStatus) {
                return status.getDesc();
            }
        }
        return AccountStatus.NORMAL.getDesc();
    }

    /**
     * 账户是否正常可用
     */
    public boolean isAccountEnabled() {
        return this.accountStatus == AccountStatus.NORMAL.getCode()
                && !Boolean.TRUE.equals(this.deleted);
    }
}