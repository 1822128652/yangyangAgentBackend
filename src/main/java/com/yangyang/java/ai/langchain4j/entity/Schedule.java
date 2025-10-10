package com.yangyang.java.ai.langchain4j.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 9.29 新增——号源实体类
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("schedule")
public class Schedule {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 科室 */
    private String department;

    /** 医生姓名，可选 */
    private String doctorName;

    /** 就诊日期 (yyyy-MM-dd) */
    private String date;

    /** 就诊时间段（上午 / 下午） */
    private String time;

    /** 总号源数 */
    private Integer total;

    /** 剩余号源数 */
    private Integer remaining;
}
