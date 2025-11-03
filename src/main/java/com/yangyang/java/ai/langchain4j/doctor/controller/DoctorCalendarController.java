package com.yangyang.java.ai.langchain4j.doctor.controller;

import com.yangyang.java.ai.langchain4j.doctor.common.Result;
import com.yangyang.java.ai.langchain4j.doctor.entity.Doctor;
import com.yangyang.java.ai.langchain4j.doctor.service.DoctorAppointmentService;
import com.yangyang.java.ai.langchain4j.doctor.service.DoctorService;
import com.yangyang.java.ai.langchain4j.entity.Appointment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 10.29 新增
@RestController
@RequestMapping("/doctor/calendar")
@Tag(name = "医生日程管理")
public class DoctorCalendarController {

    private final Logger log = LoggerFactory.getLogger(DoctorCalendarController.class);

    @Autowired
    private DoctorService doctorService;
    @Autowired
    private DoctorAppointmentService doctorAppointmentService;

    @GetMapping("/week")
    @Operation(summary = "获取指定医生某一周的预约")
    public Result<?> getWeekAppointment(
            @RequestParam("weekStart") String weekStart,
            @RequestParam("weekEnd") String weekEnd,
            HttpServletRequest request
    ) {
        log.info("weekStart=" + weekStart + ", weekEnd=" + weekEnd);

        Integer doctorId = (Integer) request.getAttribute("doctorId");
        if (doctorId == null) {
            return Result.error("请先登录！");
        }
        Doctor doctor = doctorService.getById(doctorId);
        List<Appointment> appointments = doctorAppointmentService.getByDoctorNameAndDate(doctor.getName(), weekStart, weekEnd);

        return Result.success(appointments);
    }

}
