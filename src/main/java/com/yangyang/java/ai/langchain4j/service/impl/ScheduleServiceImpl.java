package com.yangyang.java.ai.langchain4j.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yangyang.java.ai.langchain4j.entity.Appointment;
import com.yangyang.java.ai.langchain4j.entity.Schedule;
import com.yangyang.java.ai.langchain4j.mapper.ScheduleMapper;
import com.yangyang.java.ai.langchain4j.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// 9.29 新增——号源管理
@Service
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper, Schedule> implements ScheduleService {

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Override
    public Schedule getSchedule(String department, String doctorName, String date, String time) {
        LambdaQueryWrapper<Schedule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Schedule::getDepartment, department)
                .eq(Schedule::getDate, date)
                .eq(Schedule::getTime, time);

        if(doctorName != null && !doctorName.isEmpty()){
            queryWrapper.eq(Schedule::getDoctorName, doctorName);
        }
        return this.getOne(queryWrapper, false); // false：多条记录时返回第一条，不抛出异常
    }

    @Override
    public boolean decrementRemaining(long ScheduleId) {
        Schedule schedule = this.getById(ScheduleId);
        if(schedule == null || schedule.getRemaining() <= 0){
            return false;
        }
        schedule.setRemaining(schedule.getRemaining() - 1);
        return this.updateById(schedule);
    }

    @Override
    public List<Schedule> getSchedulesByDoctorName(String doctorName) {
        LambdaQueryWrapper<Schedule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Schedule::getDoctorName, doctorName);
        return this.list(queryWrapper);
    }

    @Override
    public boolean updateSchedule(Schedule schedule) {
        return this.updateById(schedule);
    }

    @Override
    public void addSchedule(Schedule schedule) {
        this.save(schedule);
    }

    @Override
    public void incrementRemaining(Long id) {
        Schedule schedule = this.getById(id);
        schedule.setRemaining(schedule.getRemaining() + 1);
        this.updateById(schedule);
    }

    @Override
    public List<Schedule> getDoctorNamesByDepartment(Appointment appointment) {
        QueryWrapper<Schedule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("department", appointment.getDepartment());
        queryWrapper.eq("date", appointment.getDate());
        queryWrapper.eq("time", appointment.getTime());
        return scheduleMapper.selectList(queryWrapper);
    }
}
