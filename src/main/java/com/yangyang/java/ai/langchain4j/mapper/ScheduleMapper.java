package com.yangyang.java.ai.langchain4j.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yangyang.java.ai.langchain4j.entity.Schedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

// 9.29 新增——号源管理
@Mapper
public interface ScheduleMapper extends BaseMapper<Schedule> {

}

