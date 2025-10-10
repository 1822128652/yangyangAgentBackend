package com.yangyang.java.ai.langchain4j.doctor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yangyang.java.ai.langchain4j.entity.Appointment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DoctorAppointmentMapper extends BaseMapper<Appointment> {
}
