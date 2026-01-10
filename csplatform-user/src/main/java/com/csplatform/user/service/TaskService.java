package com.csplatform.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.csplatform.user.entities.Task;
import com.csplatform.user.entities.User;

import java.util.List;
import java.util.Map;

/**
 * @Author WangXing
 * @Date 2026/1/2 9:10
 * @PackageName:com.csplatform.user.service
 * @ClassName: TaskService
 * @Version 1.0
 */

public interface TaskService extends IService<Task> {

    /**
     * 获得所有的用户任务
     * @param id
     * @return
     */
    List<Task> getAllTasksByUserId(Long id);

    /**
     * 单个完成任务
     */
    void finishOneTask(Long id);


    /**
     * 添加任务
     * @param task
     */
    void addTaskByUserId(Task task);

    /**
     * 删除任务
     * @param id
     */
    void deleteById(Long id);

    /**
     * 完成任务
     * @param id
     */
    void achieveTaskById(Long id);

    /**
     * 热力图数据
     * @param userId
     * @return
     */
    List<Map<String, Object>> getRecentYearHeatmapDataByTaskCount(Long userId);

}
