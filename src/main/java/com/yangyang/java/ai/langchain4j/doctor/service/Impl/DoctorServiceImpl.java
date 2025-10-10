package com.yangyang.java.ai.langchain4j.doctor.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yangyang.java.ai.langchain4j.doctor.entity.Doctor;
import com.yangyang.java.ai.langchain4j.doctor.mapper.DoctorMapper;
import com.yangyang.java.ai.langchain4j.doctor.service.DoctorService;
import org.apache.pdfbox.tools.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class DoctorServiceImpl extends ServiceImpl<DoctorMapper, Doctor> implements DoctorService {

    @Autowired
    private DoctorMapper doctorMapper;

    @Override
    public Doctor login(String account, String password) {
        Doctor doctor = this.lambdaQuery().eq(Doctor::getAccount, account).one();
        // 密码校验，BCrypt 从存储的哈希值中提取盐值，使用相同的盐值对用户输入的明文密码进行哈希计算，比较是否一致
        if (doctor != null && BCrypt.checkpw(password, doctor.getPassword())) {
            return doctor;
        }
        return null;
    }

    // 10.10 add 检查用户名是否已经存在
    @Override
    public boolean checkUsernameExists(String newUsername) {
        Doctor doctor = doctorMapper.selectByAccount(newUsername);
        if(doctor != null){
            return true;
        }
        return false;
    }

    // 10.10 add 修改用户名
    @Override
    public boolean updateUsername(Integer doctorId, String newUsername) {
        Doctor doctor = new Doctor();
        doctor.setId(Long.valueOf(doctorId));
        doctor.setAccount(newUsername);
        return doctorMapper.updateById(doctor) > 0;
    }

    // 10.10 add 验证原密码是否相同
    @Override
    public boolean checkOldPwd(Integer doctorId, String oldPassword) {
        Doctor doctor = doctorMapper.selectById(doctorId);
        if(doctor != null && BCrypt.checkpw(oldPassword, doctor.getPassword())){
            return true;
        }
        return false;
    }

    // 10.10 add 修改密码
    @Override
    public boolean updatePwd(Integer doctorId, String newPassword) {
        Doctor doctor = doctorMapper.selectById(doctorId);
        doctor.setId(Long.valueOf(doctorId));
        doctor.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        return doctorMapper.updateById(doctor) > 0;
    }
}
