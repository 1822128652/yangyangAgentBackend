package com.yangyang.java.ai.langchain4j.doctor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yangyang.java.ai.langchain4j.doctor.entity.Doctor;
import com.yangyang.java.ai.langchain4j.doctor.entity.vo.DoctorVO;
import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface DoctorMapper extends BaseMapper<Doctor> {

    @Select("select * from doctors where account = #{newUsername}")
    Doctor selectByAccount(String newUsername);

    // 11.4 add
    @Select("select * from doctors where id != #{currentDoctorId}")
    List<Doctor> getDoctorListExceptSelf(Long currentDoctorId);
}
