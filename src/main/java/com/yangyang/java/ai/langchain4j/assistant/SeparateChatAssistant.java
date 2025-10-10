/*
package com.yangyang.java.ai.langchain4j.assistant;


import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

//实现隔离聊天记忆
@AiService(
        wiringMode = EXPLICIT,
        chatModel = "qwenChatModel",
        chatMemoryProvider = "chatMemoryProvider",
        tools = "calculatorTools"   //配置工具
)
public interface SeparateChatAssistant {
    //@SystemMessage("你叫奶龙，今天是{{current_date}}")//系统提示词，如果切换了系统提示词，记忆就会丢失
    @SystemMessage(fromResource = "prompt-template.txt")
    String chat(@MemoryId int memoryId, @UserMessage String userMessage);

    @UserMessage("你叫秧秧，是我的女朋友，{{args}}")
    String chat2(@MemoryId int memoryId, @V("args") String userMessage);    //如果有多个参数，必须用@V注解表示userMessage参数的名字

    @SystemMessage(fromResource = "yangyang.txt")
    String chat3(@MemoryId int memoryId,
                 @UserMessage String userMessage,
                 @V("username") String username,
                 @V("age") int age
    );
}
*/
