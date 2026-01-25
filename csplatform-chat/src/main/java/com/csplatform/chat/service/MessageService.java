package com.csplatform.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.csplatform.chat.entities.Message;
import com.csplatform.chat.entities.vo.MessageVO;
import jakarta.websocket.Session;

import java.util.List;
import java.util.Map;

/**
 * @Author WangXing
 * @Date 2026/1/23 13:21
 * @PackageName:com.csplatform.chat.service
 * @ClassName: MessageService
 * @Version 1.0
 */

public interface MessageService extends IService<Message> {

    /**
     * 获得用户信息
     * @param userA
     * @param userB
     * @return
     */
    List<MessageVO> getMessages(Long userA, Long userB);

    /**
     * 发送信息
     * @param userid
     * @param message
     */
    void sendMessage(Long userid, String message, Map<Long, Session> sessionMap);
}
