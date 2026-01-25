package com.csplatform.chat.controller;

import com.csplatform.chat.entities.Message;
import com.csplatform.chat.entities.vo.ContactVO;
import com.csplatform.chat.entities.vo.MessageVO;
import com.csplatform.chat.service.MessageService;
import com.csplatform.chat.service.RelationService;
import com.csplatform.common.resp.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author WangXing
 * @Date 2026/1/23 13:16
 * @PackageName:com.csplatform.chat.controller
 * @ClassName: ChatController
 * @Version 1.0
 */
@RestController
@RequestMapping("/chat")
@Slf4j
public class ChatController {

    @Autowired
    private RelationService relationService;

    @Autowired
    private MessageService messageService;


    @GetMapping("/add_friend")
    public Result<String> addFriend(@RequestParam("userA") Long userA,@RequestParam("userB") Long userB){
        log.info("加好友：{} {}",userA,userB);
        relationService.makeFriends(userA,userB);
        return Result.success("OK");
    }


    @GetMapping("/del_friend")
    public Result<String> delFriend(@RequestParam("userA") Long userA,@RequestParam("userB") Long userB){
        log.info("删除好友: {} {}",userA,userB);
        relationService.delFriends(userA,userB);
        return Result.success("OK");
    }

    @GetMapping("/online")
    public Result<String> online(@RequestParam("userId") Long userId){
        log.info("用户:{} 上线",userId);
        relationService.online(userId);
        return Result.success("OK");
    }

    @GetMapping("/offLine")
    public Result<String> offLine(@RequestParam("userId") Long userId){
        log.info("用户下线");
        relationService.offline(userId);
        return Result.success("OK");
    }

    @GetMapping("/get_contacts")
    public Result<List<ContactVO>> getContacts(@RequestParam("userId") Long userId){
        log.info("获得用户:{} 的通讯录",userId);

        return Result.success(relationService.getContacts(userId));
    }

    @GetMapping("/get_messages")
    public Result<List<MessageVO>> getMessages(@RequestParam("userA") Long userA , @RequestParam("userB") Long userB){
        log.info("获得用户:{},{}的聊天记录",userA,userB);
        return Result.success(messageService.getMessages(userA,userB));
    }

}
