package com.yangyang.java.ai.langchain4j.doctor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yangyang.java.ai.langchain4j.doctor.entity.Doctor;
import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Select;

@Mapper
public interface DoctorMapper extends BaseMapper<Doctor> {

    @Select("select * from doctors where account = #{newUsername}")
    Doctor selectByAccount(String newUsername);
}
