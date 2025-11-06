package com.yangyang.java.ai.langchain4j.doctor.entity.vo;

import lombok.Data;

// 11.4 add
@Data
public class DoctorVO {
    private Long id;
    private String realName; // 医生姓名
    private String avatar; // 头像URL
    private String department; // 科室（可选，前端可显示）
}
