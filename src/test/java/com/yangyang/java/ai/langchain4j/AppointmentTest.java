/*
package com.yangyang.java.ai.langchain4j;

import com.yangyang.java.ai.langchain4j.entity.Appointment;
import com.yangyang.java.ai.langchain4j.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AppointmentTest {

    @Autowired
    private AppointmentService appointmentService;

    @Test
    void testGetOne() {
        Appointment appointment = new Appointment();
        appointment.setUsername("惊蛰");
        appointment.setIdCard("123456789012345678");
        appointment.setDepartment("出去玩");
        appointment.setDate("2025-07-02");
        appointment.setTime("上午");
        Appointment appointmentDB = appointmentService.getOne(appointment);
        System.out.println(appointmentDB);
    }

    @Test
    void testSave() {
        Appointment appointment = new Appointment();
        appointment.setUsername("惊蛰");
        appointment.setIdCard("123456789012345678");
        appointment.setDepartment("出去玩");
        appointment.setDate("2025-07-02");
        appointment.setTime("上午");
        appointment.setDoctorName("秧秧");
        appointmentService.save(appointment);
    }
    @Test
    void testRemoveById() {
        appointmentService.removeById(1L);
    }
}
*/
