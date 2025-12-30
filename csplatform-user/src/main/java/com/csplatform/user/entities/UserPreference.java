package com.csplatform.user.entities;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

/**
 * 用户偏好设置实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_preference")
public class UserPreference {

    public enum PreferenceType {
        LANGUAGE("language", "语言偏好"),
        THEME("theme", "主题偏好"),
        DIFFICULTY("difficulty", "难度偏好"),
        NOTIFICATION("notification", "通知偏好"),
        ACCESSIBILITY("accessibility", "无障碍设置"),
        PLAYBACK("playback", "播放设置"),
        UI("ui", "界面设置");

        private final String code;
        private final String desc;

        PreferenceType(String code, String desc) {
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

    @TableField("user_id")
    private Long userId;

    @TableField("preference_type")
    private String preferenceType;

    @TableField("preference_key")
    private String preferenceKey;

    @TableField("preference_value")
    private String preferenceValue;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}