package com.yangyang.java.ai.langchain4j.doctor.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yangyang.java.ai.langchain4j.doctor.mapper.DoctorScheduleMapper;
import com.yangyang.java.ai.langchain4j.doctor.service.DoctorScheduleService;
import com.yangyang.java.ai.langchain4j.entity.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorScheduleServiceImpl extends ServiceImpl<DoctorScheduleMapper, Schedule> implements DoctorScheduleService {

    @Autowired
    private DoctorScheduleMapper doctorScheduleMapper;

    @Override
    public List<String> getAllDepartments() {
        return doctorScheduleMapper.getAllDepartments();
    }
}
