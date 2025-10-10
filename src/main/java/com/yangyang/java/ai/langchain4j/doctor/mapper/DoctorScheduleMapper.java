package com.yangyang.java.ai.langchain4j.doctor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yangyang.java.ai.langchain4j.entity.Schedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DoctorScheduleMapper extends BaseMapper<Schedule> {

    @Select("select distinct department from schedule")
    List<String> getAllDepartments();
}

