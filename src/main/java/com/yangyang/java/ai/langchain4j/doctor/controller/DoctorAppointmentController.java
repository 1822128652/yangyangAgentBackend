package com.yangyang.java.ai.langchain4j.doctor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yangyang.java.ai.langchain4j.doctor.service.DoctorAppointmentService;
import com.yangyang.java.ai.langchain4j.entity.Appointment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/doctor/appointment")
@Tag(name = "医生预约管理")
public class DoctorAppointmentController {

    @Autowired
    private DoctorAppointmentService doctorAppointmentService;

    private Logger log = LoggerFactory.getLogger(DoctorAppointmentController.class);

    @Operation(summary = "查询预约记录")
    @GetMapping("/list")
    public Page<Appointment> listAppointment(
            @RequestParam(value = "doctorName", required = false) String doctorName,
            @RequestParam(value = "department", required = false) String department,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "time", required = false) String time,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
     ) {
        log.info("查询预约记录......" + doctorName + " " + department + " " + date + " " + time );
        return doctorAppointmentService.searchAppointments(doctorName, department, date, time, page, size);
    }

}
