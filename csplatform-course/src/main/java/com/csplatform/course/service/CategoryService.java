package com.csplatform.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.csplatform.course.entity.Category;

import java.util.List;
import java.util.Map;

/**
 * @Author WangXing
 * @Date 2026/1/11 13:41
 * @PackageName:com.csplatform.course.service
 * @ClassName: CategoryService
 * @Version 1.0
 */

public interface CategoryService extends IService<Category> {

    /**
     * 获得全部的课程种类
     * @return
     */
    List<Map<String,String>> getAll();

}
