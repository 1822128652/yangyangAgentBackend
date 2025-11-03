package com.yangyang.java.ai.langchain4j.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yangyang.java.ai.langchain4j.entity.Appointment;
import com.yangyang.java.ai.langchain4j.entity.Schedule;

import java.util.List;

// 9.29 新增——号源管理
public interface ScheduleService extends IService<Schedule> {

    // 获取某个医生/科室在指定日期和时间段的排班
    Schedule getSchedule(String department, String doctorName, String date, String time);

    // 扣减剩余号源
    boolean decrementRemaining(long ScheduleId);

    // 查询医生的所有排班
    List<Schedule> getSchedulesByDoctorName(String doctorName);

    // 更新排班
    boolean updateSchedule(Schedule schedule);

    // 新增号源（仅供测试）
    void addSchedule(Schedule tempSchedule);

    // 取消预约回退号源
    void incrementRemaining(Long id);

    // 10.29 新增——根据科室名查询可满足的医生姓名
    List<Schedule> getDoctorNamesByDepartment(Appointment appointment);
}
