package com.csplatform.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.csplatform.chat.entities.Relation;
import com.csplatform.chat.entities.vo.ContactVO;

import java.util.List;

/**
 * @Author WangXing
 * @Date 2026/1/23 13:17
 * @PackageName:com.csplatform.chat.service
 * @ClassName: ChatService
 * @Version 1.0
 */

public interface RelationService extends IService<Relation> {

    //添加好友
    void makeFriends(Long userA, Long userB);

    /**
     * 删除好友
     * @param userA
     * @param userB
     */
    void delFriends(Long userA, Long userB);

    /**
     * 上线操作
     * @param userId
     */
    void online(Long userId);

    /**
     * 下线
     * @param userId
     */
    void offline(Long userId);

    /**
     * 获得用户通讯录
     * @param userId
     * @return
     */
    List<ContactVO> getContacts(Long userId);
}
