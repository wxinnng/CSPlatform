package com.csplatform.course.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csplatform.course.entity.Category;
import com.csplatform.course.mapper.CategoryMapper;
import com.csplatform.course.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author WangXing
 * @Date 2026/1/11 13:42
 * @PackageName:com.csplatform.course.service.impl
 * @ClassName: CategoryServiceImpl
 * @Version 1.0
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {


    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Map<String,String>> getAll() {
        List<Category> categories = categoryMapper.selectList(null);
        // 转换为 List<Map<String, String>>
        // 假设 category 有 getContent() 方法
        // 假设 id 是 Long 类型
        return categories.stream()
                .map(category -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("label", category.getContent());  // 假设 category 有 getContent() 方法
                    map.put("value", category.getId().toString());  // 假设 id 是 Long 类型
                    return map;
                })
                .collect(Collectors.toList());
    }
}
