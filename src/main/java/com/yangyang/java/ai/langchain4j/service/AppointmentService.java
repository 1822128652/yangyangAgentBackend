package com.yangyang.java.ai.langchain4j.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yangyang.java.ai.langchain4j.entity.Appointment;

import java.util.List;


public interface AppointmentService extends IService<Appointment> {

    Appointment getOne(Appointment appointment);

    // 9.29 新增——查询挂号记录
    List<Appointment> queryByContact(String idCard, String contact);
}
