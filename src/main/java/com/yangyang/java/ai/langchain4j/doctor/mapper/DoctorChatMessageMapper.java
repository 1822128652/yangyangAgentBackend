package com.yangyang.java.ai.langchain4j.doctor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yangyang.java.ai.langchain4j.doctor.entity.DoctorChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

// 11.4 add
@Mapper
public interface DoctorChatMessageMapper extends BaseMapper<DoctorChatMessage> {

    List<DoctorChatMessage> selectChatHistory(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);

    void markAsRead(@Param("senderId") Long senderId,@Param("receiverId") Long receiverId);

    // 11.6 add
    List<Map<String, Object>> selectUnreadCountBySender(@Param("receiverId") Long receiverId);
}
