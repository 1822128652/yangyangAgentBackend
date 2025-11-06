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
-- 10.29 新增
alter table doctors add column avatar VARCHAR(500) NOT NULL
    DEFAULT ''
    comment '用户头像';
alter table doctors add column create_time datetime
    default now()
    comment '创建时间';
-- 添加管理员(admin、admin)
insert into `doctors` values (1, '超级管理员', '管理员', 'admin', '$2a$10$g72GLm1Y2KNzL1mqk7kBm.j.9qDt94A7OVl4BNK7A.Q87cbP8c2Ci', 'https://yangyang-1.oss-cn-beijing.aliyuncs.com/d69e0dd7-faf0-4b1b-b18d-2c878676c4f0.jpg', now());

-- 11.3 新增
CREATE TABLE `doctor_chat_message` (
                                       `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID（主键）',
                                       `sender_id` bigint NOT NULL COMMENT '发送者医生ID（关联doctors.id）',
                                       `receiver_id` bigint NOT NULL COMMENT '接收者医生ID（关联doctors.id）',
                                       `content` varchar(500) NOT NULL COMMENT '消息内容（文本）',
                                       `send_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
                                       `is_read` tinyint DEFAULT 0 COMMENT '是否已读（0-未读，1-已读）',
                                       PRIMARY KEY (`id`),
                                       INDEX idx_sender_receiver (`sender_id`,`receiver_id`),
                                       INDEX idx_receiver_unread (`receiver_id`,`is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息表';
