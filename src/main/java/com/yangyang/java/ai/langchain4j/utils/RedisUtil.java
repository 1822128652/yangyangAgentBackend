package com.yangyang.java.ai.langchain4j.utils;

import com.yangyang.java.ai.langchain4j.doctor.mapper.DoctorChatMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 11.6 add Redis 工具类
@Component
public class RedisUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private DoctorChatMessageMapper doctorChatMessageMapper;

    // 递增未读计数
    public Long incrUnreadCount(Long receiverId, Long senderId)  {
        String key = "unread:doctor:"  + receiverId;
        return stringRedisTemplate.opsForHash().increment(key, senderId.toString(), 1);
    }

    // 重置未读计数（切换会话的时候调用）
    public void resetUnreadCount(Long receiverId, Long senderId) {
        String key = "unread:doctor:" + receiverId;
        stringRedisTemplate.opsForHash().put(key, senderId.toString(), "0");
    }

    // 批量获取用户所有会话的未读计数
    public Map<Long, Integer> getUnreadCounts(Long receiverId) {
        String key = "unread:doctor:" + receiverId;
        HashOperations<String, Object, Object> hashOps = stringRedisTemplate.opsForHash();
        Map<Object, Object> entries = hashOps.entries(key);

        Map<Long, Integer> result = new HashMap<>();
        entries.forEach((k, v) -> {
            // 修改：处理不同数据类型
            Long keyLong = (k instanceof String) ? Long.valueOf((String) k) : (Long) k;
            Integer valueInt = (v instanceof String) ? Integer.valueOf((String) v) : (Integer) v;
            result.put(keyLong, valueInt);
        });
        return result;
    }

    // 从数据库同步未读计数到Redis
    public void syncUnreadFromToRedis(Long receiverId) {
        String key = "unread:doctor:" + receiverId;
        stringRedisTemplate.delete(key);    // 先清空旧数据

        // 从数据库查询该用户的未读消息（按照发送方分组计数）
        List<Map<String, Object>> dbUnreadList = doctorChatMessageMapper.selectUnreadCountBySender(receiverId);
        for(Map<String, Object> item : dbUnreadList) {
            Long senderId = ((Number) item.get("sender_id")).longValue();
            Integer count = ((Number) item.get("unread_count")).intValue();
            stringRedisTemplate.opsForHash().put(key, senderId.toString(), count.toString());
        }
    }

}
