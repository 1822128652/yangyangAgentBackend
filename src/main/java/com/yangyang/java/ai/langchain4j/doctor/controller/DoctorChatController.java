package com.yangyang.java.ai.langchain4j.doctor.controller;

import com.yangyang.java.ai.langchain4j.doctor.common.Result;
import com.yangyang.java.ai.langchain4j.doctor.config.webSocket.DoctorChatWebSocket;
import com.yangyang.java.ai.langchain4j.doctor.entity.dto.SendMessageDTO;
import com.yangyang.java.ai.langchain4j.doctor.entity.vo.DoctorVO;
import com.yangyang.java.ai.langchain4j.doctor.entity.vo.MessageVO;
import com.yangyang.java.ai.langchain4j.doctor.mapper.DoctorChatMessageMapper;
import com.yangyang.java.ai.langchain4j.doctor.service.DoctorChatService;
import com.yangyang.java.ai.langchain4j.doctor.service.DoctorService;
import com.yangyang.java.ai.langchain4j.utils.RedisUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// 11.4 add
@RestController
@RequestMapping("/doctor/chat")
@Tag(name = "后台聊天")
@Slf4j
public class DoctorChatController {

    @Autowired
    private DoctorChatService doctorChatService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private DoctorChatMessageMapper doctorChatMessageMapper;

    @Autowired
    private RedisUtil redisUtil;


    // 发送消息
    @PostMapping("/send")
    @Operation(summary = "发送聊天消息")
    public Result<?> send(@RequestBody SendMessageDTO sendMessageDTO, HttpServletRequest request) {
        try {
            // 获取当前登录的医生 id
            Integer senderDoctorId = (Integer) request.getAttribute("doctorId");
            Long doctorId = Long.valueOf(senderDoctorId);

            // 发送消息并获取返回结果
            MessageVO messageVO = doctorChatService.sendMessage(doctorId, sendMessageDTO);

            // 构建接收方的消息
            MessageVO receiverVO = new MessageVO();
            receiverVO.setId(messageVO.getId());
            receiverVO.setSenderId(doctorId);
            receiverVO.setSenderName(messageVO.getSenderName());
            receiverVO.setAvatar(messageVO.getAvatar());
            receiverVO.setContent(messageVO.getContent());
            receiverVO.setSendTime(messageVO.getSendTime());
            receiverVO.setIsSelf(false);    // 不是自己发送的（接收）

            // 通过WebSocket实时推送给接收者
            DoctorChatWebSocket.pushMessage(sendMessageDTO.getReceiverId(), receiverVO);

            // 11.6 新增：推送未读更新给接收方（实时更新红点）
            Integer newUnreadCount = redisUtil.getUnreadCounts(sendMessageDTO.getReceiverId()).getOrDefault(doctorId, 0);
            DoctorChatWebSocket.pushUnreadUpdate(sendMessageDTO.getReceiverId(), doctorId, newUnreadCount);

            return Result.success(messageVO);
        }catch (RuntimeException e) {
            log.error("发送消息失败：", e);
            return Result.error(e.getMessage());
        }catch (Exception e){
            log.error("发送消息异常：", e);
            return Result.error("发送消息失败");
        }
    }


    // 获取历史消息
    @GetMapping("/history")
    @Operation(summary = "获取聊天历史记录")
    public Result<?> getChatHistory(HttpServletRequest request, @RequestParam("targetDoctorId") Long targetDoctorId) {
        try {
            // 获取当前登录的医生 Id
            Integer currentDoctorId = (Integer) request.getAttribute("doctorId");
            Long doctorId = Long.valueOf(currentDoctorId);

            // 查询聊天历史
            List<MessageVO> history = doctorChatService.getChatHistory(doctorId, targetDoctorId);
            return Result.success(history);
        } catch (Exception e) {
            log.error("获取聊天历史失败：", e);
            return Result.error("获取聊天历史失败");
        }
    }


    // 获取医生列表（排除自身）
    @GetMapping("/doctorList")
    @Operation(summary = "获取聊天医生列表（排除自身）")
    public Result<?> getChatDoctorList(HttpServletRequest request) {
        try {
            // 获取当前登录的医生 Id
            Integer currentDoctorId = (Integer) request.getAttribute("doctorId");
            Long doctorId = Long.valueOf(currentDoctorId);

            // 查询医生列表
            List<DoctorVO> doctorList = doctorService.getDoctorListExceptSelf(doctorId);
            return Result.success(doctorList);
        } catch (Exception e) {
            log.error("获取医生列表失败：", e);
            return Result.error("获取医生列表失败");
        }
    }


    // 11.6 add 统计当前登录医生的所有未读消息条数
    @GetMapping("/unreadCount")
    @Operation(summary = "统计当前登录医生的所有未读消息条数")
    public Result<?> getUnreadCount(HttpServletRequest request) {
        try {
            // 获取当前登录的医生 Id
            Integer currentDoctorId = (Integer) request.getAttribute("doctorId");
            Long doctorId = Long.valueOf(currentDoctorId);

            // 统计未读消息条数
            Map<Long, Integer> counts = doctorChatService.getUnreadCounts(doctorId);
            return Result.success(counts);
        } catch (Exception e) {
            log.error("统计未读消息失败：", e);
            return Result.error("统计未读消息失败");
        }
    }

    // 11.6 add 标记当前选择的医生消息为已读 (同步Redis和数据库)
    @PostMapping("/markAsRead")
    @Operation(summary = "标记当前选择的医生消息为已读")
    public Result<?> markAsRead(@RequestParam("senderId") Long senderId, HttpServletRequest httpservletRequest){
        try {
            // 获取当前登录的医生 Id
            Integer currentDoctorId = (Integer) httpservletRequest.getAttribute("doctorId");
            Long receiverId = Long.valueOf(currentDoctorId);

            doctorChatService.markAsRead(senderId, receiverId);
            return Result.success("标记已读成功");
        } catch (Exception e) {
            log.error("标记消息为已读失败：", e);
            return Result.error("标记消息为已读失败");
        }
    }

    // 11.6 add 初始化未读计数 (登录/上线时调用，同步数据库到Redis)
    @GetMapping("/initUnread")
    @Operation(summary = "初始化未读计数")
    public Result<?> initUnread(HttpServletRequest httpservletRequest){
        try {
            // 获取当前登录的医生 Id
            Integer currentDoctorId = (Integer) httpservletRequest.getAttribute("doctorId");
            Long doctorId = Long.valueOf(currentDoctorId);

            doctorChatService.initUnreadCount(doctorId);
            return Result.success("初始化未读计数成功");
        } catch (Exception e) {
            log.error("初始化未读计数失败：", e);
            return Result.error("初始化未读计数失败");
        }
    }

}
