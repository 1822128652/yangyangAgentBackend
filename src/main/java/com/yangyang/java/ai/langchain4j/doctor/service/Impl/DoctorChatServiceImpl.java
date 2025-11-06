package com.yangyang.java.ai.langchain4j.doctor.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yangyang.java.ai.langchain4j.doctor.entity.Doctor;
import com.yangyang.java.ai.langchain4j.doctor.entity.DoctorChatMessage;
import com.yangyang.java.ai.langchain4j.doctor.entity.dto.SendMessageDTO;
import com.yangyang.java.ai.langchain4j.doctor.entity.vo.MessageVO;
import com.yangyang.java.ai.langchain4j.doctor.mapper.DoctorChatMessageMapper;
import com.yangyang.java.ai.langchain4j.doctor.service.DoctorChatService;
import com.yangyang.java.ai.langchain4j.doctor.service.DoctorService;
import com.yangyang.java.ai.langchain4j.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 11.4 add
@Service
@Slf4j
public class DoctorChatServiceImpl extends ServiceImpl<DoctorChatMessageMapper, DoctorChatMessage> implements DoctorChatService {

    @Autowired
    private DoctorChatMessageMapper doctorChatMessageMapper;

    @Autowired
    private DoctorService doctorService;
    @Autowired
    private RedisUtil redisUtil;

    // 11.6 update
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageVO sendMessage(Long senderDoctorId, SendMessageDTO sendMessageDTO) {
        // 1. 验证接收者是否存在
        Doctor receiver = doctorService.getById(sendMessageDTO.getReceiverId());
        if (receiver == null) {
            throw new RuntimeException("接收医生不存在");
        }

        // 2. 验证发送者是否存在（双重保险）
        Doctor sender = doctorService.getById(senderDoctorId);
        if (sender == null) {
            throw new RuntimeException("发送者信息不存在");
        }

        // 3. 保存消息到数据库
        DoctorChatMessage message = new DoctorChatMessage(senderDoctorId, sendMessageDTO.getReceiverId(), sendMessageDTO.getContent());
        doctorChatMessageMapper.insert(message);

        // 11.6 新增：Redis 递增接收方的未读计数
        redisUtil.incrUnreadCount(sendMessageDTO.getReceiverId(), senderDoctorId);

        // 4. 构建返回给发送者的MessageVO
        MessageVO vo = new MessageVO();
        vo.setId(message.getId());
        vo.setSenderId(senderDoctorId);
        vo.setSenderName(sender.getName());
        vo.setAvatar(sender.getAvatar());
        vo.setContent(message.getContent());
        vo.setSendTime(message.getSendTime());
        vo.setIsSelf(true); // 发送者自己的消息
        return vo;
    }

    @Override
    public List<MessageVO> getChatHistory(Long currentDoctorId, Long targetDoctorId) {
        // 1. 查询双向聊天记录
        List<DoctorChatMessage> messages = doctorChatMessageMapper.selectChatHistory(currentDoctorId, targetDoctorId);
        if(messages.isEmpty()){
            log.info("没有聊天记录哦！");
            return null;
        }

        // 2. 标记对方发送的消息为已读
        doctorChatMessageMapper.markAsRead(targetDoctorId, currentDoctorId);

        // 3. 转换为MessageVO（标记是否是自己的消息）
        return messages.stream().map(message -> {
            MessageVO vo = new MessageVO();
            vo.setId(message.getId());
            vo.setSenderId(message.getSenderId());

            // 获取发送者信息
            Doctor sender = doctorService.getById(message.getSenderId());
            vo.setSenderName(sender.getName());
            vo.setAvatar(sender.getAvatar());

            vo.setContent(message.getContent());
            vo.setSendTime(message.getSendTime());
            vo.setIsSelf(message.getSenderId().equals(currentDoctorId));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public Map<Long, Integer> getUnreadCounts(Long doctorId) {
        return redisUtil.getUnreadCounts(doctorId);
    }

    @Override
    public void markAsRead(Long senderId, Long receiverId) {
        //  数据库批量标记已读
        doctorChatMessageMapper.markAsRead(senderId, receiverId);
        //  redis 重置未读计数为 0
        redisUtil.resetUnreadCount(receiverId, senderId);
    }

    @Override
    public void initUnreadCount(Long doctorId) {
        redisUtil.syncUnreadFromToRedis(doctorId);
    }



/*    @Override
    public Map<Long, Integer> countUnreadMessages(Long doctorId) {
        // 构造查询条件
        QueryWrapper<DoctorChatMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("receiver_id", doctorId);
        queryWrapper.eq("is_read", 0);
        queryWrapper.groupBy("sender_id");
        queryWrapper.select("sender_id", "count(id) as unread_count");

        // 获取查询结果
        List<Map<String, Object>> list = doctorChatMessageMapper.selectMaps(queryWrapper);

        Map<Long, Integer> unreadCountMap = new HashMap<>();

        // 将结果存在一个Map中
        for(Map<String, Object> map : list) {
            Long senderId = ((Number) map.get("sender_id")).longValue();
            Integer unreadCount = ((Number) map.get("unread_count")).intValue();
            unreadCountMap.put(senderId, unreadCount);
        }
        return unreadCountMap;
    }*/
}
