package com.csplatform.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csplatform.chat.api.UserService;
import com.csplatform.chat.entities.Message;
import com.csplatform.chat.entities.Relation;
import com.csplatform.chat.entities.vo.ContactVO;
import com.csplatform.chat.entities.vo.SearchUserVO;
import com.csplatform.chat.enums.MessageCategoryEnum;
import com.csplatform.chat.enums.RelationStatusEnum;
import com.csplatform.chat.mapper.RelationMapper;
import com.csplatform.chat.service.MessageService;
import com.csplatform.chat.service.RelationService;
import com.csplatform.chat.util.RedisUtil;
import com.csplatform.common.exception.BusinessException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author WangXing
 * @Date 2026/1/23 13:22
 * @PackageName:com.csplatform.chat.service.impl
 * @ClassName: RelationServiceImpl
 * @Version 1.0
 */
@Service
@Slf4j
public class RelationServiceImpl extends ServiceImpl<RelationMapper, Relation> implements RelationService {


    @Autowired
    private RelationMapper relationMapper;

    @Resource
    private RedisUtil redisUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;


    // 用户在线状态key前缀
    private static final String USER_ONLINE_KEY_PREFIX = "user:online:";
    // 在线用户集合key
    private static final String ONLINE_USERS_SET_KEY = "online:users";
    // 用户最后活跃时间key前缀
    private static final String USER_LAST_ACTIVE_KEY_PREFIX = "user:last_active:";



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void makeFriends(Long userA, Long userB) {

        if(userB == 1001)
            throw new BusinessException("我永远是你的好友~");

        //先查询，是否已经是好友了
        LambdaQueryWrapper<Relation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Relation::getUserA , userA)
                .eq(Relation::getUserB ,userB);

        Relation relation1 = relationMapper.selectOne(wrapper);

        if(relation1 != null && Objects.equals(relation1.getStatus(), RelationStatusEnum.ENABLED.getCode()))
            throw new BusinessException("已经是好友了！");

        if(relation1 != null){
            relation1.setStatus(RelationStatusEnum.ENABLED.getCode());
            int i = relationMapper.updateById(relation1);
            if(i < 1)
                throw new BusinessException("服务器异常");
        }else{
            Relation relation = new Relation(null, userA, userB, LocalDateTime.now(), RelationStatusEnum.ENABLED.getCode());

            //加一次好友要创建两条数据
            int insert = relationMapper.insert(relation);

            if(insert < 1)
                throw new BusinessException("服务器异常");

            relation.setUserA(userB);
            relation.setUserB(userA);
            relation.setId(null);

            int insert1 = relationMapper.insert(relation);
            if(insert1 < 1)
                throw new BusinessException("服务器异常");
            //插入第一条信息
            Message message = new Message(null, userB, userA, "我们已经是好友了，快来聊天吧！", MessageCategoryEnum.TEXT.getCode(), RelationStatusEnum.ENABLED.getCode(), LocalDateTime.now());
            boolean insert2 = messageService.save(message);
            if(!insert2)
                throw new BusinessException("服务器异常！");
        }

    }

    /**
     * 用户上线
     * @param userId 用户ID
     */
    @Override
    public void online(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        // 1. 设置用户在线状态，过期时间到当天午夜
        String userOnlineKey = USER_ONLINE_KEY_PREFIX + userId;
        redisUtil.setex(userOnlineKey, "online", redisUtil.getSecondsToMidnight());

        // 2. 添加到在线用户集合
        redisUtil.sadd(ONLINE_USERS_SET_KEY, userId.toString());

        // 3. 更新用户最后活跃时间
        String lastActiveKey = USER_LAST_ACTIVE_KEY_PREFIX + userId;
        redisUtil.setex(lastActiveKey, LocalDateTime.now().toString(), redisUtil.getSecondsToMidnight());
    }

    @Override
    public List<ContactVO> getContacts(Long userId) {
        List<ContactVO> contactVOS = relationMapper.selectContactsWithLastMessage(userId);
        if(contactVOS == null || contactVOS.isEmpty()){
            return null;
        }

        contactVOS
                .stream()
                .map((item) -> {
                    //设置是否在线
                    item.setIsOnline(isOnline(item.getId()));

                    //补充头像和用户名
                    SearchUserVO data = userService.searchUser(item.getId()).getData();
                    item.setAvatarUrl(data.getAvatarUrl());
                    item.setUserName(data.getUsername());


                    //TODO::
                    item.setUnRead(1);

                    return item;
                }).collect(Collectors.toList());

        log.info("最终数据: {}",contactVOS);

        return contactVOS;

    }

    /**
     * 用户下线
     * @param userId 用户ID
     */
    public void offline(Long userId) {
        if (userId == null) return;

        // 1. 删除用户在线状态
        String userOnlineKey = USER_ONLINE_KEY_PREFIX + userId;
        redisUtil.del(userOnlineKey);

        // 2. 从在线用户集合中移除
        redisUtil.srem(ONLINE_USERS_SET_KEY, userId.toString());
    }


    /**
     * 检查用户是否在线
     * @param userId 用户ID
     * @return 是否在线
     */
    private boolean isOnline(Long userId) {
        if (userId == null) return false;

        String userOnlineKey = USER_ONLINE_KEY_PREFIX + userId;
        return redisUtil.exists(userOnlineKey);
    }

    @Override
    public void delFriends(Long userA, Long userB) {

        if (userA == null || userB == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        if(userB == 1001)
            throw new BusinessException("休想删了我~");

        // 构建查询条件：查找 userA-userB 或 userB-userA 的关系记录
        LambdaQueryWrapper<Relation> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w
                .eq(Relation::getUserA, userA)
                .eq(Relation::getUserB, userB)
        ).or(w -> w
                .eq(Relation::getUserA, userB)
                .eq(Relation::getUserB, userA)
        );

        // 执行删除（逻辑删除，将状态改为已删除）
        Relation relation = new Relation();
        relation.setStatus(RelationStatusEnum.DELETED.getCode()); // 使用枚举
        relation.setCreateTime(LocalDateTime.now()); // 可选：更新修改时间

        int result = relationMapper.update(relation, wrapper);

        if (result == 0) {
            throw new BusinessException("好友关系不存在或已被删除");
        }
    }
}
