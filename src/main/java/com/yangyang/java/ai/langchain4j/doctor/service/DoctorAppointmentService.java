package com.yangyang.java.ai.langchain4j.doctor.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yangyang.java.ai.langchain4j.entity.Appointment;

import java.util.List;


public interface DoctorAppointmentService extends IService<Appointment> {

    Page<Appointment> searchAppointments(String doctorName, String department, String date, String time, int page, int size);

    List<Appointment> getByDoctorNameAndDate(String name, String weekStart, String weekEnd);
}
