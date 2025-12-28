package com.csplatform.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.csplatform.file.entities.User;

/**
 * @Author WangXing
 * @Date 2025/12/28 12:52
 * @PackageName:com.csplatform.file.service
 * @ClassName: UserService
 * @Version 1.0
 */
// 1. 定义服务接口，继承 IService
public interface UserService extends IService<User> {

    String all();
}

