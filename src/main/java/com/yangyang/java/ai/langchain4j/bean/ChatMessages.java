package com.yangyang.java.ai.langchain4j.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("chat_messages")
public class ChatMessages {

    //唯一表示，映射到MongoDB文档的 _id 字段
    @Id
    private ObjectId messageId;

    //聊天记忆id
    private String memoryId;

    //存储当前聊天记录列表的json字段
    private String content;

}
