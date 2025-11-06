package com.yangyang.java.ai.langchain4j.doctor.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// 11.4 add
@Data
public class SendMessageDTO {
    @NotNull(message = "接收者ID不能为空")
    private Long receiverId; // 接收医生ID
    @NotBlank(message = "消息内容不能为空")
    private String content; // 消息文本
}
