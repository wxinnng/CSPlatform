package com.csplatform.chat.websocket;



import com.csplatform.chat.service.MessageService;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.HashMap;
import java.util.Map;

/**
 * @Author:WangXing
 * @DATE:2024/5/2
 */
@Component
@Slf4j
@ServerEndpoint("/userchat/{userid}")
public class ChatServer {

    //保存在线的会话
    private static Map<Long, Session> sessionMap = new HashMap<>();

    private static MessageService messageService;



    @Autowired
    public void setApplicationContext(MessageService messageService){
        ChatServer.messageService = messageService;
    }



    /**
     * 连接建立成功调用的方法,登录的时候调用这个方法
     */
    @OnOpen
    public void onOpen(Session session,@PathParam("userid") Long userid) {
        log.info("{} 与服务器进行连接.", userid);
        sessionMap.put(userid, session);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message,@PathParam("userid") Long userid) {
        log.info("用户：{}，发送信息：{}",userid,message);

        //交给service处理
        messageService.sendMessage(userid,message,sessionMap);

    }

    /**
     * 连接关闭调用的方法(用户下线)
     * @param userid
     */
    @OnClose
    public void onClose(@PathParam("userid") Long userid) {
        log.info("{} :关闭连接" , userid);
        sessionMap.remove(userid);
    }
}
