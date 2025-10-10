package com.yangyang.java.ai.langchain4j.doctor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("doctors")
public class Doctor {
    private Long id;
    private String name;
    private String department;
    private String account;
    private String password; // 加密存储
}
