package com.yangyang.java.ai.langchain4j.doctor.config.webSocket;

import com.alibaba.fastjson.JSON;
import com.yangyang.java.ai.langchain4j.doctor.entity.vo.MessageVO;
import com.yangyang.java.ai.langchain4j.doctor.entity.vo.UnreadUpdateVO;
import com.yangyang.java.ai.langchain4j.utils.RedisUtil;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

// 11.4 add
@Component
@ServerEndpoint("/ws/doctor/chat/{doctorId}")
@Slf4j
public class DoctorChatWebSocket {

    // 存储在线医生的WebSocket连接
    private static final ConcurrentHashMap<Long, Session> ONLINE_DOCTORS = new ConcurrentHashMap<>();

    private static RedisUtil redisUtil;
    @Autowired
    public void setRedisUtil(RedisUtil redisUtil) {
        DoctorChatWebSocket.redisUtil = redisUtil;
    }

    // 建立连接时触发
    @OnOpen
    public void onOpen(Session session, @PathParam("doctorId") Long doctorId) {
        ONLINE_DOCTORS.put(doctorId, session);
        log.info("医生[{}]建立WebSocket连接，当前在线人数：{}", doctorId, ONLINE_DOCTORS.size());
    }

    // 连接关闭时触发
    @OnClose
    public void onClose(@PathParam("doctorId") Long doctorId) {
        ONLINE_DOCTORS.remove(doctorId);
        log.info("医生[{}]断开WebSocket连接，当前在线人数：{}", doctorId, ONLINE_DOCTORS.size());
    }

    // 接收消息时触发
    @OnMessage
    public void onMessage(String message, @PathParam("doctorId") Long doctorId) {
        log.info("收到医生[{}]的消息：{}", doctorId, message);
    }

    // 连接异常时触发
    @OnError
    public void onError(Session session, Throwable error, @PathParam("doctorId") Long doctorId) {
        log.error("医生[{}]WebSocket连接异常", doctorId, error);
        ONLINE_DOCTORS.remove(doctorId);
    }

    // 主动推送消息给指定医生
    public static void pushMessage(Long doctorId, MessageVO messageVO) {
        Session session = ONLINE_DOCTORS.get(doctorId);
        if(session != null && session.isOpen()) {
            try {
                // 转换为JSON字符串发送
                String jsonMessage = JSON.toJSONString(messageVO);
                session.getBasicRemote().sendText(jsonMessage);
                log.info("推送消息给医生[{}]成功：{}", doctorId, jsonMessage);
            } catch (IOException e) {
                log.error("推送消息给医生[{}]失败", doctorId, e);
            }
        }else {
            log.warn("医生[{}]不在线，无法推送消息", doctorId);
        }
    }

    // 11.6 add 推送未读更新事件给指定医生
    public static void pushUnreadUpdate(Long doctorId, Long convId, Integer unreadCount) {
        Session session = ONLINE_DOCTORS.get(doctorId);
        if(session != null && session.isOpen()) {
            try {
                UnreadUpdateVO updateVO = new UnreadUpdateVO();
                updateVO.setConvId(convId);
                updateVO.setUnreadCount(unreadCount);
                String json = JSON.toJSONString(updateVO);
                // 给消息加类型标记，方便前端区分是“聊天消息”还是“未读更新”
                String message = "{\"type\":\"unreadUpdate\",\"data\":" + json + "}";
                session.getBasicRemote().sendText(message);
                log.info("推送未读更新给医生[{}]：convId={}, count={}", doctorId, convId, unreadCount);
            } catch (IOException e) {
                log.error("推送未读更新失败", e);
            }
        }
    }


}
