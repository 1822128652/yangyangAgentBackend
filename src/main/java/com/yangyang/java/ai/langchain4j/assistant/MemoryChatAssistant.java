/*
package com.yangyang.java.ai.langchain4j.assistant;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

@AiService(
        wiringMode = EXPLICIT,
        chatModel = "qwenChatModel",
        chatMemory = "chatMemory"
)//指定模型，防止冲突
public interface MemoryChatAssistant {
    @UserMessage("你叫张龙峰，江西人，请用江西抚州话回答问题，并添加一些表情符号 {{message}}")
    String chat(@V("message") String userMessage);  //@V注解，设置占位符
}
*/
