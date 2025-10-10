/*
package com.yangyang.java.ai.langchain4j;

import com.yangyang.java.ai.langchain4j.bean.ChatMessages;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@SpringBootTest
public class MongoCrudTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    */
/*插入文件*//*

*/
/*    @Test
    public void testInsert() {
        mongoTemplate.insert(new ChatMessages(1L,"聊天记录"));
    }*//*

    @Test
    public void testInsert2() {
        ChatMessages chatMessages = new ChatMessages();
        chatMessages.setContent("聊天记录表");
        mongoTemplate.insert(chatMessages);
    }

    */
/*查询文件*//*

    @Test
    public void testFind() {
        ChatMessages chatMessages = mongoTemplate.findById("684a32be87b6753affe7d177",ChatMessages.class);
        System.out.println(chatMessages);
    }

    */
/*修改文件*//*

    @Test
    public void testUpdate() {
        Criteria criteria = Criteria.where("_id").is("684a32be87b6753affe7d177");
        Query query = new Query(criteria);
        Update update = new Update();
        update.set("content","和秧秧的聊天记录");

        //修改或新增
        mongoTemplate.upsert(query,update,ChatMessages.class);
    }
    @Test
    public void testUpdate2() {
        Criteria criteria = Criteria.where("_id").is("100");
        Query query = new Query(criteria);
        Update update = new Update();
        update.set("content","和惊蛰的聊天记录");

        //修改或新增
        mongoTemplate.upsert(query,update,ChatMessages.class);
    }

    */
/*删除文件*//*

    @Test
    public void testDelete() {
        Criteria criteria = Criteria.where("_id").is("100");
        Query query = new Query(criteria);
        mongoTemplate.remove(query,ChatMessages.class);
    }

}
*/
