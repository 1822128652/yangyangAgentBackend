package com.yangyang.java.ai.langchain4j.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MergeRequest {
    private String tempDirName;
    private String fileName;
}
