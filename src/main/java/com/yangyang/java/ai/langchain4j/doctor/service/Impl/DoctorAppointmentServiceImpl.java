package com.yangyang.java.ai.langchain4j.doctor.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yangyang.java.ai.langchain4j.doctor.mapper.DoctorAppointmentMapper;
import com.yangyang.java.ai.langchain4j.doctor.service.DoctorAppointmentService;
import com.yangyang.java.ai.langchain4j.entity.Appointment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class DoctorAppointmentServiceImpl extends ServiceImpl<DoctorAppointmentMapper, Appointment> implements DoctorAppointmentService {

    private Logger log = LoggerFactory.getLogger(DoctorAppointmentServiceImpl.class);

    @Override
    public Page<Appointment> searchAppointments(String doctorName, String department, String date, String time, int page, int size) {
        LambdaQueryWrapper<Appointment> query = new LambdaQueryWrapper<>();

        if(doctorName != null && !doctorName.isEmpty()){
            query.like(Appointment::getDoctorName, doctorName);
        }
        if(department != null && !department.isEmpty()){
            query.eq(Appointment::getDepartment, department);
        }
        if(date != null && !date.isEmpty()){
            query.eq(Appointment::getDate, date);
        }
        if(time != null && !time.isEmpty()){
            query.eq(Appointment::getTime, time);
        }

        Page<Appointment> pageInfo = new Page<>(page, size);
        this.page(pageInfo, query);
        log.info("分页结果：current={}, size={}, total={}, records={}", pageInfo.getCurrent(), pageInfo.getSize(), pageInfo.getTotal(), pageInfo.getRecords().size());
        return pageInfo;
    }
}
