package com.csplatform.course.controller;

import com.csplatform.common.resp.Result;
import com.csplatform.course.entity.Category;
import com.csplatform.course.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Author WangXing
 * @Date 2026/1/11 13:40
 * @PackageName:com.csplatform.course.controller
 * @ClassName: CategoryControlelr
 * @Version 1.0
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {


    @Autowired
    private CategoryService categoryService;

    @GetMapping("/get")
    public Result<List<Map<String,String>>> getAllCategories(){
        log.info("获得全部种类");
        return Result.success(categoryService.getAll());
    }

}
