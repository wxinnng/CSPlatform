package com.csplatform.chat.service.impl;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csplatform.chat.entities.Message;
import com.csplatform.chat.entities.vo.MessageVO;
import com.csplatform.chat.enums.RelationStatusEnum;
import com.csplatform.chat.mapper.MessageMapper;
import com.csplatform.chat.service.MessageService;
import com.csplatform.common.exception.BusinessException;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author WangXing
 * @Date 2026/1/23 13:21
 * @PackageName:com.csplatform.chat.service.impl
 * @ClassName: MessageServiceImpl
 * @Version 1.0
 */
@Service
@Slf4j
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    private List<MessageVO> convertToMessageVOList(List<Message> messageList) {
        if (CollectionUtils.isEmpty(messageList)) {
            return new ArrayList<>();
        }

        // 按日期分组并转换
        Map<LocalDate, List<Message>> dateGroupMap = messageList.stream()
                .collect(Collectors.groupingBy(message ->
                        message.getCreateTime().toLocalDate()
                ));

        return dateGroupMap.entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<Message> dayMessages = entry.getValue();

                    // 按时间升序排序（从早到晚）
                    dayMessages.sort(Comparator.comparing(Message::getCreateTime));

                    MessageVO vo = new MessageVO();
                    vo.setDate(date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
                    vo.setMsgs(dayMessages.stream().map(this::convertToMessageItem).collect(Collectors.toList()));

                    return vo;
                })
                .sorted(Comparator.comparing(MessageVO::getDate)) // 改为按日期升序（昨天在今天前面）
                .collect(Collectors.toList());
    }

    private MessageVO.MessageItem convertToMessageItem(Message message) {
        MessageVO.MessageItem item = new MessageVO.MessageItem();
        item.setId(message.getId());
        item.setSendUser(message.getSendUser());
        item.setContent(message.getContent());
        item.setCategory(message.getCategory());
        item.setCreateTime(message.getCreateTime()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli());
        return item;
    }

    public List<MessageVO> getMessages(Long userA, Long userB) {
        if (userA == null || userB == null) {
            throw new BusinessException("参数异常！");
        }

        // 查询数据库 - 保持升序
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w1 -> w1
                .eq(Message::getSendUser, userA)
                .eq(Message::getReceiveUser, userB)
        ).or(w2 -> w2
                .eq(Message::getSendUser, userB)
                .eq(Message::getReceiveUser, userA)
        ).orderByAsc(Message::getCreateTime); // 保持升序

        List<Message> messages = messageMapper.selectList(wrapper);

        // 转换并保持正确的顺序
        return convertToMessageVOList(messages);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendMessage(Long userid, String message,Map<Long, Session> sessionMap) {
        //1.格式化信息
        Message targetMessage = JSONUtil.toBean(message, Message.class);
        if(targetMessage == null || targetMessage.getReceiveUser() == null){
            throw new BusinessException("参数异常!");
        }

        //2.拿到数据,插入到数据库
        targetMessage.setCreateTime(LocalDateTime.now());
        targetMessage.setStatus(RelationStatusEnum.ENABLED.getCode());
        int insert = messageMapper.insert(targetMessage);
        if(insert < 1)
            throw new BusinessException("服务器异常！");

        //3.判断用户是否在线
        Session session = sessionMap.get(targetMessage.getReceiveUser());
        if(session != null){
            try {
                session.getBasicRemote().sendText(JSONUtil.toJsonStr(targetMessage));
            } catch (IOException e) {
                e.printStackTrace();
                //虽然推送失败，但是不影响消息入库
                log.error("实时信息同步失败");
            }
        }
    }

}
