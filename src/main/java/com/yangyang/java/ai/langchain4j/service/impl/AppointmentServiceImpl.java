package com.yangyang.java.ai.langchain4j.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yangyang.java.ai.langchain4j.entity.Appointment;
import com.yangyang.java.ai.langchain4j.mapper.AppointmentMapper;
import com.yangyang.java.ai.langchain4j.service.AppointmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements AppointmentService {

    /**
     * 查询挂号记录是否存在
     * @param appointment
     * @return
     */
    @Override
    public Appointment getOne(Appointment appointment) {
        LambdaQueryWrapper<Appointment> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Appointment::getUsername, appointment.getUsername());
        queryWrapper.eq(Appointment::getIdCard, appointment.getIdCard());
        queryWrapper.eq(Appointment::getContact, appointment.getContact());     // 新增校验手机号（同时修改系统提示词，让联系方式为必选）
        queryWrapper.eq(Appointment::getDepartment, appointment.getDepartment());
        queryWrapper.eq(Appointment::getDate, appointment.getDate());
        queryWrapper.eq(Appointment::getTime, appointment.getTime());

        return baseMapper.selectOne(queryWrapper);

    }

    // 9.29 新增——查询挂号记录（根据身份证信息和手机号）
    @Override
    public List<Appointment> queryByContact(String idCard, String contact) {
        LambdaQueryWrapper<Appointment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Appointment::getIdCard, idCard);
        queryWrapper.eq(Appointment::getContact, contact);
        return baseMapper.selectList(queryWrapper);
    }
}
