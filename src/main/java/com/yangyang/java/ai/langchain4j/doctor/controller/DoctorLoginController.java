package com.yangyang.java.ai.langchain4j.doctor.controller;

import com.yangyang.java.ai.langchain4j.doctor.common.Result;
import com.yangyang.java.ai.langchain4j.doctor.entity.ChangePasswordRequest;
import com.yangyang.java.ai.langchain4j.doctor.entity.Doctor;
import com.yangyang.java.ai.langchain4j.doctor.entity.UpdateUsernameRequest;
import com.yangyang.java.ai.langchain4j.doctor.service.DoctorService;
import com.yangyang.java.ai.langchain4j.utils.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/doctor")
@Tag(name = "医生后台系统")
public class DoctorLoginController {

    @Autowired
    private DoctorService doctorService;

    private Logger log = LoggerFactory.getLogger(DoctorLoginController.class);

    // 10.9 update 登录
    @PostMapping("/login")
    @Operation(summary = "登录")
    public Result<?> login(@RequestBody Map<String, String> body) {
        String account = body.get("account");
        String password = body.get("password");
        Doctor doctor = doctorService.login(account, password);
        // 10.9 update 加上 JWT 逻辑
        if(doctor != null) {
            // 构造 token
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", doctor.getId());
            claims.put("name", doctor.getName());
            claims.put("department", doctor.getDepartment());

            // 生成 JWT token
            String token = JwtUtils.generateToken(claims);
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("doctor", claims);
            log.info("登录成功！");
            return Result.success(result);
        }else {
            return Result.error("账号或密码错误！请重新输入");
        }
    }


    // 10.9 add 获取医生的信息
    @GetMapping("/info")
    @Operation(summary = "获取医生信息")
    public Result<?> getDoctorInfo(HttpServletRequest request) {
        try {
            Integer doctorId = (Integer) request.getAttribute("doctorId");
            //String doctorName = (String) request.getAttribute("doctorName");
            //String department = (String) request.getAttribute("department");

            if (doctorId == null) {
                return Result.error("未登录或登录已过期");
            }

            // 从数据库获取完整的医生信息
            Doctor doctor = doctorService.getById(doctorId);
            if (doctor == null) {
                return Result.error("医生信息不存在");
            }

            Map<String, Object> doctorInfo = new HashMap<>();
            doctorInfo.put("id", doctor.getId());
            doctorInfo.put("username", doctor.getAccount()); // 账号作为用户名
            doctorInfo.put("realName", doctor.getName());    // 姓名
            doctorInfo.put("department", doctor.getDepartment());

            return Result.success(doctorInfo);
        } catch (Exception e) {
            log.error("获取医生信息失败:", e);
            return Result.error("获取医生信息失败");
        }
    }


    // 10.10 add 修改用户名
    @PutMapping ("/updateUsername")
    @Operation(summary = "修改用户名")
    public Result<?> updateUsername(@RequestBody @Valid UpdateUsernameRequest updateUsernameRequest, HttpServletRequest request) {
        try {
            Integer doctorId = (Integer) request.getAttribute("doctorId");
//            String doctorUserName = (String) request.getAttribute("doctorUsername");
            String doctorName = (String) request.getAttribute("doctorName");
            if(doctorId == null) {
                return Result.error("未登录或者登录已经过期");
            }

            String newUsername = updateUsernameRequest.getUsername();

            // 用户名必须和当前用户名不一样（前端已经实现）
//            if(newUsername.equals(doctorUserName)) {
//                return Result.error("新用户名必须和当前用户名不同");
//            }

            // 检查新用户名是否已经存在
            boolean exists = doctorService.checkUsernameExists(newUsername);
            if(exists) {
                return Result.error("用户名已存在");
            }

            // 更新用户名
            boolean success = doctorService.updateUsername(doctorId, newUsername);
            if(success) {
                log.info("医生{}修改用户名为{}", doctorName, newUsername);
                return Result.success("用户名修改成功");
            }else {
                return Result.error("修改用户名失败");
            }
        }catch (Exception e){
            log.error("修改用户名失败:", e);
            return Result.error("修改用户名失败");
        }
    }


    // 10.10 add 修改密码
    @PutMapping("/changePassword")
    @Operation(summary = "修改密码")
    public Result<?> changePwd(@RequestBody @Valid ChangePasswordRequest changePasswordRequest, HttpServletRequest request) {
        try {
            Integer doctorId = (Integer) request.getAttribute("doctorId");
            String doctorName = (String) request.getAttribute("doctorName");
            if(doctorId == null) {
                return Result.error("未登录或登录已过期");
            }
            String oldPassword = changePasswordRequest.getOldPassword();
            String newPassword = changePasswordRequest.getNewPassword();

            // 限制密码长度
            if(newPassword.length() < 6) {
                return Result.error("密码不能少于6位");
            }

            // 验证原密码
            boolean result = doctorService.checkOldPwd(doctorId, oldPassword);
            if(!result) {
                return Result.error("原密码错误");
            }

            // 更新密码
            boolean success = doctorService.updatePwd(doctorId, newPassword);
            if(success) {
                log.info("医生{}修改密码成功",doctorName);
                return Result.success("密码修改成功");
            }else {
                return Result.error("修改密码失败");
            }
        }catch (Exception e){
            log.error("修改密码失败：",e);
            return Result.error("修改密码失败");
        }
    }


}
