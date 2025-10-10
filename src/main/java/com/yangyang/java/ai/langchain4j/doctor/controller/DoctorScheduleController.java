package com.yangyang.java.ai.langchain4j.doctor.controller;

import com.yangyang.java.ai.langchain4j.doctor.service.DoctorScheduleService;
import com.yangyang.java.ai.langchain4j.entity.Schedule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctor/schedule")
@Tag(name = "医生排班管理")
public class DoctorScheduleController {

    @Autowired
    private DoctorScheduleService scheduleService;

    private Logger log = LoggerFactory.getLogger(DoctorScheduleController.class);

    // 查询排班
    @Operation(summary = "查询排班")
    @GetMapping
    public List<Schedule> listSchedules() {
        log.info("查询排班表......");
        return scheduleService.list();
    }

    // 新增排班
    @Operation(summary = "新增排班")
    @PostMapping
    public boolean addSchedule(@RequestBody Schedule schedule) {
        // 新增时默认 remaining = total
        log.info("新增排班......");
        schedule.setRemaining(schedule.getTotal());
        return scheduleService.save(schedule);
    }

    // 修改排班
    @Operation(summary = "修改排班")
    @PutMapping("/{id}")
    public boolean updateSchedule(@PathVariable("id") Long id, @RequestBody Schedule schedule) {
        log.info("修改排班......");
        schedule.setId(id);
        return scheduleService.updateById(schedule);
    }

    // 删除排班
    @Operation(summary = "删除排班")
    @DeleteMapping("/{id}")
    public boolean deleteSchedule(@PathVariable("id") Long id) {
        log.info("删除排班......");
        return scheduleService.removeById(id);
    }

    // 查询号源所属的科室列表
    @Operation(summary = "查询号源所属的科室列表")
    @GetMapping("/list")
    public List<String> listAllDepartments() {
        return scheduleService.getAllDepartments();
    }
}
