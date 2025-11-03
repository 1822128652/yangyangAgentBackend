package com.yangyang.java.ai.langchain4j.doctor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

// 10.29 修改
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("doctors")
public class Doctor {
    private Long id;
    // 名字
    private String name;
    // 科室
    private String department;
    // 用户名
    private String account;
    // 密码
    private String password; // 加密存储
    // 头像
    private String avatar;
    // 创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
