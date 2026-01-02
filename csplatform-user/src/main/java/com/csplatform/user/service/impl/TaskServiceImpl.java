package com.csplatform.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csplatform.common.exception.BusinessException;
import com.csplatform.user.entities.Task;
import com.csplatform.user.mapper.TaskMapper;
import com.csplatform.user.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * @Author WangXing
 * @Date 2026/1/2 9:11
 * @PackageName:com.csplatform.user.service.impl
 * @ClassName: TaskServiceImpl
 * @Version 1.0
 */
@Service
@Slf4j
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService  {

    @Autowired
    private TaskMapper taskMapper;

    public List<Task> getAllTasksByUserId(Long userId) {

        LocalDate today = LocalDate.now();
        String todayStr = today.toString();

        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("is_completed", false)
                .apply("DATE(created_at) = DATE('" + todayStr + "')")  // 使用 apply
                .orderByAsc("start_time");

        return taskMapper.selectList(queryWrapper);
    }

    @Override
    public void addTaskByUserId(Task task) {

        int insert = taskMapper.insertSelective(task);
        if(insert < 1)
            throw new BusinessException("添加失败，请稍后再试！");

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishOneTask(Long id) {
        // 参数验证
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("任务ID不能为空");
        }

        // 获取任务
        Task task = taskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException(404, "任务不存在或已被删除");
        }

        // 检查任务状态
        if (Boolean.TRUE.equals(task.getIsCompleted())) {
            log.warn("任务已完成，任务ID: {}");
            return; // 已完成的直接返回，不做处理
        }

        // 设置完成状态
        LocalDateTime now = LocalDateTime.now();

        // 创建更新对象
        Task updateTask = new Task();
        updateTask.setId(id);
        updateTask.setIsCompleted(true);
        updateTask.setCompletionRate(100);
        updateTask.setUpdatedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()));

        // 更新任务
        int rows = taskMapper.updateById(updateTask);
        if (rows <= 0) {
            throw new BusinessException("更新任务状态失败");
        }

        // 记录完成日志
        log.info("任务已完成，任务ID: {}, 任务内容: {}, 完成时间: {}",
                id, task.getContent(), now);

        // 可以触发后续事件（如发送通知、统计等）
        afterTaskFinished(task);
    }

    //TODO:统计操作
    private void afterTaskFinished(Task task) {

    }

}
