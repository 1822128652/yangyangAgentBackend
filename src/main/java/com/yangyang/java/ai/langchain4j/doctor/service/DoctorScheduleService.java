package com.yangyang.java.ai.langchain4j.doctor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yangyang.java.ai.langchain4j.entity.Schedule;

import java.util.List;

public interface DoctorScheduleService extends IService<Schedule> {
    // 查询号源所属的科室列表
    List<String> getAllDepartments();
}
