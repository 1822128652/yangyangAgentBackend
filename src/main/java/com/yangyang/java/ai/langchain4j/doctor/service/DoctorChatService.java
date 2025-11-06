package com.yangyang.java.ai.langchain4j.doctor.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yangyang.java.ai.langchain4j.doctor.entity.Doctor;
import com.yangyang.java.ai.langchain4j.doctor.entity.DoctorChatMessage;
import com.yangyang.java.ai.langchain4j.doctor.entity.dto.SendMessageDTO;
import com.yangyang.java.ai.langchain4j.doctor.entity.vo.MessageVO;

import java.util.List;
import java.util.Map;

public interface DoctorChatService extends IService<DoctorChatMessage> {

    MessageVO sendMessage(Long senderDoctorId, SendMessageDTO sendMessageDTO);

    List<MessageVO> getChatHistory(Long currentDoctorId, Long targetDoctorId);

    //Map<Long, Integer> countUnreadMessages(Long doctorId);

    Map<Long, Integer> getUnreadCounts(Long doctorId);

    void markAsRead(Long senderId, Long receiverId);

    void initUnreadCount(Long doctorId);
}
