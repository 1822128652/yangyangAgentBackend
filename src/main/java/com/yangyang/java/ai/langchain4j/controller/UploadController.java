package com.yangyang.java.ai.langchain4j.controller;

import com.yangyang.java.ai.langchain4j.doctor.common.Result;
import com.yangyang.java.ai.langchain4j.utils.AliYunOSSUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

// 10.29 新增
@RestController
@RequestMapping("/yangyang/upload")
@Tag(name = "文件上传OSS")
public class UploadController {

    private static final Logger log = LoggerFactory.getLogger(UploadController.class);
    @Autowired
    private AliYunOSSUtils aliyunOSSUtils;


    @Operation(summary = "上传图片")
    @PostMapping("/photo")
    public Result<?> getUserAvatar(@RequestParam("file") MultipartFile file)throws Exception {
        log.info("上传图片:{}", file.getOriginalFilename());
        String url =  aliyunOSSUtils.upload(file.getBytes(),file.getOriginalFilename());
        log.info("图片上传的URL为：{}",url);
        return Result.success(url);
    }
}
