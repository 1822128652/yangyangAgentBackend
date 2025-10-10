package com.yangyang.java.ai.langchain4j;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 10.9 修改
@SpringBootApplication
@MapperScan("com.yangyang.java.ai.langchain4j.mapper")
public class YangyangApp {

    public static void main(String[] args) {
        SpringApplication.run(YangyangApp.class,args);
    }
}
