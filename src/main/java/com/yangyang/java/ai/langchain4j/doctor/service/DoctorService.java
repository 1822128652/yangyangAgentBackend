package com.yangyang.java.ai.langchain4j.doctor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yangyang.java.ai.langchain4j.doctor.entity.Doctor;

public interface DoctorService extends IService<Doctor> {

    Doctor login(String account, String password);

    // 10.10 add
    boolean checkUsernameExists(String newUsername);

    // 10.10 add
    boolean updateUsername(Integer doctorId, String newUsername);

    // 10.10 add
    boolean checkOldPwd(Integer doctorId, String oldPassword);

    // 10.10 add
    boolean updatePwd(Integer doctorId, String newPassword);

    // 10.29 add
    void updateAvatar(Integer doctorId, String avatarUrl);
}
