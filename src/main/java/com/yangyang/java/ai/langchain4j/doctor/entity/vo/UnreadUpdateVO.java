package com.yangyang.java.ai.langchain4j.doctor.entity.vo;

import lombok.Data;

//  11.6  add 未读更新推送 VO
@Data
public class UnreadUpdateVO {

    private Long convId;    // senderId
    private Integer unreadCount;    // 最新未读条数
}
