package com.csplatform.chat.entities;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author WangXing
 * @Date 2026/1/23 13:09
 * @PackageName:com.csplatform.chat.entities
 * @ClassName: Message
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("message")
public class Message {

    private Long id;

    private Long sendUser;

    private Long receiveUser;

    private String content;

    private Integer category;  // 1.文字 2.图片 3.表情， 4 文件

    private Integer status;    // 1.已接收  2.未查看  3.发送失败

    private LocalDateTime createTime;
}
