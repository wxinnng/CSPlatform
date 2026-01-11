package com.csplatform.user.controller;

import com.csplatform.common.exception.BusinessException;
import com.csplatform.common.resp.Result;
import com.csplatform.user.entities.Task;
import com.csplatform.user.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author WangXing
 * @Date 2026/1/2 9:18
 * @PackageName:com.csplatform.user.controller
 * @ClassName: TaskController
 * @Version 1.0
 */

@RestController
@RequestMapping("/task")
@Slf4j
public class TaskController {

    @Autowired
    private TaskService taskService;

    /**
     * 通过用户ID查找任务
     * @param id
     * @return
     */
    @GetMapping("/get_all/{id}")
    public Result<List<Task>> getAllTasksByUserId(@PathVariable("id") Long id){
        log.info("获取用户{}的今日任务",id);
        //获得所有用户任务
        List<Task> tasksByUserId = taskService.getAllTasksByUserId(id);

        //返回对象
        return Result.success(tasksByUserId);
    }

    /**
     * 添加用户任务
     * @param task
     * @return
     */
    @PostMapping("/add")
    public Result<String> addTaskByUserId(@RequestBody Task task){
        log.info("添加用户信息{}",task);
        taskService.addTaskByUserId(task);
        return Result.success("添加成功！");
    }


    /**
     * 删除任务
     */
    @GetMapping("/delete/{id}")
    public Result<String> deleteTaskById(@PathVariable("id") Long id){
        log.info("删除ID:{} 的任务!",id);
        //删除操作
        taskService.deleteById(id);
        return Result.success("删除成功！");
    }


    /**
     * 完成任务
     */
    @GetMapping("/achieve/{id}")
    public Result<String> achieveTaskById(@PathVariable("id") Long id){
        log.info("完成任务：{}",id);
        taskService.achieveTaskById(id);
        return Result.success("操作成功！");
    }
    /**
     * 获取用户热力图数据（按任务数量）
     */
    @GetMapping("/heatmap")
    public Result<List<Map<String, Object>>> getHeatmapData(@RequestParam("userId") Long userId) {
        List<Map<String, Object>> heatmapData = taskService.getRecentYearHeatmapDataByTaskCount(userId);
        return Result.success(heatmapData);
    }
}
