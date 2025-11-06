package com.yangyang.java.ai.langchain4j.doctor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

// 11.4 add
@Data
@TableName("doctor_chat_message")
public class DoctorChatMessage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long senderId; // 发送者ID
    private Long receiverId; // 接收者ID
    private String content; // 消息内容
    private LocalDateTime sendTime; // 发送时间
    private Integer isRead; // 0-未读，1-已读

    // 构造方法（便于创建消息）
    public DoctorChatMessage(Long senderId, Long receiverId, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.sendTime = LocalDateTime.now();
        this.isRead = 0;
    }

}
