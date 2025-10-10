/*
package com.yangyang.java.ai.langchain4j.config;

import com.yangyang.java.ai.langchain4j.store.MongoChatMemoryStore;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeparateChatAssistantConfig {

    @Autowired
    private MongoChatMemoryStore chatMemoryStore;

    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        return memoryId -> MessageWindowChatMemory
                .builder()
                .id(memoryId)
                .maxMessages(100)
                //.chatMemoryStore(new InMemoryChatMemoryStore()) //以键值对（hashMap）的方式存储（可以省略，则以linkedList方式存储）
                .chatMemoryStore(chatMemoryStore)
                .build();
    }
}
*/
