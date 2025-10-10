package com.yangyang.java.ai.langchain4j.doctor.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUsernameRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;
}
