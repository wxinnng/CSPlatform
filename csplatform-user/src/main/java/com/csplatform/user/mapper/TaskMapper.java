package com.csplatform.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csplatform.user.entities.Task;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author WangXing
 * @Date 2026/1/2 9:12
 * @PackageName:com.csplatform.user.mapper
 * @ClassName: TaskMapper
 * @Version 1.0
 */

//package com.csplatform.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csplatform.user.entities.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 任务 Mapper 接口
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {

    /**
     * 插入非空字段
     */
    int insertSelective(Task task);

    /**
     * 批量插入任务
     */
    int batchInsert(@Param("list") List<Task> tasks);

    /**
     * 根据用户ID查询任务列表
     */
    @Select("SELECT * FROM tasks WHERE user_id = #{userId} AND is_deleted = 0 ORDER BY created_at DESC")
    List<Task> selectByUserId(@Param("userId") Long userId);

    /**
     * 查询用户今日任务
     */
    @Select("SELECT * FROM tasks WHERE user_id = #{userId} AND DATE(created_at) = CURDATE() AND is_deleted = 0 ORDER BY start_time ASC")
    List<Task> selectTodayTasks(@Param("userId") Long userId);

    /**
     * 查询用户未完成任务
     */
    @Select("SELECT * FROM tasks WHERE user_id = #{userId} AND is_completed = false AND is_deleted = 0 ORDER BY priority ASC, end_time ASC")
    List<Task> selectIncompleteTasks(@Param("userId") Long userId);

    /**
     * 根据优先级查询任务
     */
    @Select("SELECT * FROM tasks WHERE user_id = #{userId} AND priority = #{priority} AND is_deleted = 0 ORDER BY created_at DESC")
    List<Task> selectByPriority(@Param("userId") Long userId, @Param("priority") Integer priority);

    /**
     * 更新任务完成状态
     */
    @Select("UPDATE tasks SET is_completed = #{completed}, completion_rate = #{completionRate}, updated_at = NOW() WHERE id = #{id} AND user_id = #{userId}")
    int updateCompletionStatus(@Param("id") Long id,
                               @Param("userId") Long userId,
                               @Param("completed") Boolean completed,
                               @Param("completionRate") Integer completionRate);

    /**
     * 更新任务进度
     */
    @Select("UPDATE tasks SET completion_rate = #{completionRate}, updated_at = NOW() WHERE id = #{id} AND user_id = #{userId}")
    int updateCompletionRate(@Param("id") Long id,
                             @Param("userId") Long userId,
                             @Param("completionRate") Integer completionRate);

    /**
     * 软删除任务
     */
    @Select("UPDATE tasks SET is_deleted = 1, updated_at = NOW() WHERE id = #{id} AND user_id = #{userId}")
    int softDelete(@Param("id") Long id, @Param("userId") Long userId);


    /**
     * 查询用户最近一年每天完成任务数量（热力图数据格式）
     * 返回格式: [{date: 时间戳, value: 任务数量}]
     */
    List<Map<String, Object>> findHeatmapDataByTaskCount(@Param("userId") Long userId);




}
