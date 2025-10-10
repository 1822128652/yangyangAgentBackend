/*
package com.yangyang.java.ai.langchain4j;

import com.yangyang.java.ai.langchain4j.assistant.MemoryChatAssistant;
import com.yangyang.java.ai.langchain4j.assistant.SeparateChatAssistant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PromptTest {

    @Autowired
    private SeparateChatAssistant separateChatAssistant;
    @Autowired
    private MemoryChatAssistant memoryChatAssistant;

    @Test
    public void testSystemMessage() {
        String answer = separateChatAssistant.chat(4,"我叫游嘉超！你是谁呀？");
        System.out.println(answer);
    }

    @Test
    public void testSystemMessage1() {
        String answer = memoryChatAssistant.chat("你是谁呀？");
        System.out.println(answer);
        String answer1 = memoryChatAssistant.chat("我是赖鑫涛！今年21啦！");
        System.out.println(answer1);
        String answer2 = memoryChatAssistant.chat("你是谁呀？");
        System.out.println(answer2);
        String answer3 = memoryChatAssistant.chat("我是谁呀？");
        System.out.println(answer3);
    }
    @Test
    public void testV(){
        String answer = separateChatAssistant.chat2(1,"你是谁呀");
        System.out.println(answer);
        String answer1 = separateChatAssistant.chat2(1,"你对我的感觉是什么样的呀？");
        System.out.println(answer1);
        String answer2 = separateChatAssistant.chat2(1,"我可以牵着你的手嘛？");
        System.out.println(answer2);
    }
    @Test
    public void testYangyang() {
        //从数据库获取用户信息（此处省略）
        String username = "惊蛰";
        int age = 20;

        String answer1 = separateChatAssistant.chat3(5,"早上好呀！秧秧！",username,age);
        System.out.println(answer1);
        String answer2 = separateChatAssistant.chat3(5,"今天也要开开心心呀！我今天也很喜欢你！",username,age);
        System.out.println(answer2);
        String answer3 = separateChatAssistant.chat3(5,"我可以提一个请求嘛，如果期末考试之后，你有空的话，我们可不可以一起出去走走呀？就我们两个",username,age);
        System.out.println(answer3);
    }

}
*/
