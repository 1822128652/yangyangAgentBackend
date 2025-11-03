CREATE DATABASE `agent`;
 USE `agent`;

 drop table if exists `appointment`;
 CREATE TABLE `appointment` (
 `id` BIGINT NOT NULL AUTO_INCREMENT,
 `username` VARCHAR(50) NOT NULL,
 `id_card` VARCHAR(18) NOT NULL,
 `department` VARCHAR(50) NOT NULL,
 `date` VARCHAR(10) NOT NULL,
 `time` VARCHAR(10) NOT NULL,
 `doctor_name` VARCHAR(50) DEFAULT NULL,
 PRIMARY KEY (`id`)
 );
ALTER TABLE appointment ADD COLUMN contact VARCHAR(32) DEFAULT NULL ;
-- 按科室/日期/时段建立普通索引，便于统计/查询
CREATE INDEX idx_appointment_slot ON appointment(`department`, `date`, `time`);

drop table if exists `schedule`;
CREATE TABLE `schedule` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    department VARCHAR(50) NOT NULL,      -- 科室
    doctor_name VARCHAR(50) NULL,         -- 医生，可选
    date DATE NOT NULL,                   -- 就诊日期
    time ENUM('上午', '下午') NOT NULL,   -- 就诊时间段
    total INT NOT NULL,                   -- 总号源数
    remaining INT NOT NULL,               -- 剩余号源数
    UNIQUE (department, doctor_name, date, time) -- 唯一约束，防止重复
);


drop table if exists `doctors`;
CREATE TABLE `doctors` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  department VARCHAR(100) NOT NULL,
  account VARCHAR(50) UNIQUE NOT NULL,              -- 账号
  password VARCHAR(255) NOT NULL -- 加密存储         -- 密码
);
