package com.yangyang.java.ai.langchain4j.doctor.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

// 11.4 add
@Data
public class MessageVO {
    private Long id;
    private Long senderId; // 发送者ID
    private String senderName; // 发送者姓名
    private String avatar; // 发送者头像
    private String content; // 消息内容
    private LocalDateTime sendTime; // 发送时间
    private Boolean isSelf; // 是否是当前用户发送的消息
}