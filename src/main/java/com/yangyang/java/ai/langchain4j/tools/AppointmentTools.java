package com.yangyang.java.ai.langchain4j.tools;

import com.yangyang.java.ai.langchain4j.entity.Appointment;
import com.yangyang.java.ai.langchain4j.entity.Schedule;
import com.yangyang.java.ai.langchain4j.service.AppointmentService;
import com.yangyang.java.ai.langchain4j.service.ScheduleService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
 public class AppointmentTools {
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private ScheduleService scheduleService;

    private Logger log = LoggerFactory.getLogger(AppointmentTools.class);

    // 9.29 修改——增加号源的校验逻辑    10.29 修改——完善预约逻辑
    @Transactional(rollbackFor = Exception.class)   // 10.29 新增——增加事务注解，确保其原子性
    @Tool(name="预约挂号", value = "根据参数，先执行工具方法queryDepartment查询是否可预约，并直接给用户回答是否可预约" +
            "，并让用户确认所有预约信息，用户确认后再进行预约。" +
            "如果用户没有提供具体的医生姓名，执行工具方法getDoctorByDepartment，根据科室去查询号源，" +
            "给出所有可用的医生！如果没有任何医生在指定时间段有该科室的排班，就给用户说明“正在通知后台新增号源，请稍后再进行预约”")
    public String bookAppointment(Appointment appointment){
        //查找数据库中是否包含对应的预约记录
        Appointment appointmentDB = appointmentService.getOne(appointment);
        if(appointmentDB == null){
            appointment.setId(null);//防止大模型幻觉设置了id
            // 如果没有当前预约，那么进行号源查询
            log.info("正在查询号源......");
            Schedule schedule = scheduleService.getSchedule(appointment.getDepartment(), appointment.getDoctorName(), appointment.getDate(), appointment.getTime());
            if(schedule == null || schedule.getRemaining() <=0 ){
                return "当前时段无号源，无法预约。";
            }
            // 扣减号源（乐观锁防止并发超卖）
            boolean updated = scheduleService.decrementRemaining(schedule.getId());
            if(!updated){
                return "该号源刚被预约走了，请重新预约。";
            }
            // 保存预约
            if(appointmentService.save(appointment)){
                return "预约成功，并返回预约详情";
            }else{
                return "预约失败";
            }
        }
        return "您在相同的科室和时间已有预约";
    }


    // 10.29 新增——未指定医生，则根据科室和时间给出可选的医生
    @Tool(name = "根据科室和时间给出可选的医生", value = "用户没有提供具体的医生姓名，执行该方法，根据所给去查询号源，让用户选择可以预约的医生，如果没有查到任何数据，那么就给用户说明“正在通知后台新增号源，请稍后再进行预约”")
    public List<Schedule> getDoctorByDepartment(Appointment appointment){
        return scheduleService.getDoctorNamesByDepartment(appointment);
    }


    // 9.29 修改——号源逻辑
    @Tool(name="取消预约挂号", value = "根据参数，查询预约是否存在，如果存在则删除预约记录并返回:取消预约成功，否则返回:取消预约失败")
    public String cancelAppointment(Appointment appointment){
        Appointment appointmentDB = appointmentService.getOne(appointment);
            if(appointmentDB != null){
                //回退号源
                Schedule schedule = scheduleService.getSchedule(appointment.getDepartment(), appointment.getDoctorName(), appointment.getDate(), appointment.getTime());
                scheduleService.incrementRemaining(schedule.getId());
                //删除预约记录
                if(appointmentService.removeById(appointmentDB.getId())){
                    return "取消预约成功";
                }else{
                    return "取消预约失败";
                }
            }
                //取消失败
                return "您没有预约记录，请核对预约科室和时间";
            }


    // 9.29 修改 -- 加入号源的相关逻辑
    @Tool(name = "查询是否有号源", value="根据科室名称，日期，时间和医生查询是否有号源，并返回给用户")
    public boolean queryDepartment(
        @P(value = "科室名称") String department,
        @P(value = "日期") String date,
        @P(value = "时间，可选值：上午、下午") String time,
        @P(value = "医生名称", required = false) String doctorName){
        System.out.println("查询是否有号源");
        System.out.println("科室名称：" + department);
        System.out.println("日期：" + date);
        System.out.println("时间：" + time);
        System.out.println("医生名称：" + doctorName);
        // 引入号源查询逻辑
        log.info("正在查询号源。。。。。。");
        Schedule schedule = scheduleService.getSchedule(department, doctorName, date, time);
        if (schedule == null) {
            return false; // 没有排班记录，视为无号源
        }
        return schedule.getRemaining() > 0;
    }

    // 9.29 新增——查询挂号记录
    @Tool(name = "查询预约记录", value = "根据用户身份证号和联系方式查询其所有预约记录，但是不包括今天之前的记录，即待办预约，如果没有查询到相应记录，返回相应的提示信息")
    public List<Appointment> queryAppointmentByContact(Appointment appointment){
        String idCard = appointment.getIdCard();
        String contact = appointment.getContact();
        List<Appointment> result = appointmentService.queryByContact(idCard, contact);
        return result;
    }

    // 9.29 新增——校验身份证号和联系方式格式是否规范
    @Tool(name = "校验身份证和联系方式的规范性", value = "在用户给出身份证和联系方式的时候，请初步判断是否合规")
    public String judgeIdCardAndContact(Appointment appointment){
        String idCard = appointment.getIdCard();
        String contact = appointment.getContact();
        if(idCard.length()!=18 && idCard.length()==11)  {
            return "身份证号码错误!";
        }else if(contact.length()!=11 && contact.length()==18)  {
            return "联系方式错误!";
        }else if(idCard.length()!=18 && idCard.length()!=11)  {
            return "身份证号和联系方式均有误";
        }
        return "身份证号和电话号码无误！";
    }

}