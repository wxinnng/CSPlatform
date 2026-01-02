package com.csplatform.user.entities;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;

import lombok.NoArgsConstructor;
import java.util.Date;


/**
 * @Author WangXing
 * @Date 2026/1/2 9:07
 * @PackageName:com.csplatform.user.entities
 * @ClassName: Tasks
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tasks")
public class Task {
    /**
     * 任务ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * title
     */
    private String title;

    /**
     * 任务内容
     */
    private String content;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 是否完成
     */
    private Boolean isCompleted = false;

    /**
     * 标签
     */
    private String tags;

    /**
     * 完成度（0-100）
     */
    private Integer completionRate = 0;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdAt = new Date();

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updatedAt = new Date();

    /**
     * 优先级常量
     */
    public static class Priority {
        public static final int HIGH = 1;   // 高
        public static final int MEDIUM = 2; // 中
        public static final int LOW = 3;    // 低

        public static String getName(Integer priority) {
            if (priority == null) return "未知";
            switch (priority) {
                case HIGH: return "高";
                case MEDIUM: return "中";
                case LOW: return "低";
                default: return "未知";
            }
        }
    }
}
