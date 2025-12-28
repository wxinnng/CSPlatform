package com.csplatform.file.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csplatform.file.entities.User;
import com.csplatform.file.mapper.UserMapper;
import com.csplatform.file.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @Author WangXing
 * @Date 2025/12/28 12:53
 * @PackageName:com.csplatform.file.service.impl
 * @ClassName: UserServiceImpl
 * @Version 1.0
 */

// 2. 定义服务实现类，继承 ServiceImpl
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public String all() {
        return "xing";
    }
}

