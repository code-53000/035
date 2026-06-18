-- ========================================
-- 帆船俱乐部数据库初始化脚本
-- Database: sailing_club
-- ========================================

CREATE DATABASE IF NOT EXISTS sailing_club DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sailing_club;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ========================================
-- 1. 用户表 sys_user
-- ========================================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '登录账号',
    password VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    role VARCHAR(20) NOT NULL COMMENT '角色：MEMBER-会员 COACH-教练 ADMIN-管理员',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    KEY idx_role (role),
    KEY idx_status (status),
    KEY idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表（会员/教练/管理员通用）';

-- ========================================
-- 2. 船只表 boat
-- ========================================
DROP TABLE IF EXISTS boat;
CREATE TABLE boat (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '船只名称',
    code VARCHAR(50) NOT NULL COMMENT '船号',
    boat_type VARCHAR(50) NOT NULL COMMENT '船只类型：SAILBOAT-帆船 KAYAK-皮划艇 PADDLEBOARD-桨板 MOTORBOAT-摩托艇 CATAMARAN-双体船',
    capacity INT NOT NULL DEFAULT 1 COMMENT '可载人数',
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' COMMENT '状态：AVAILABLE-可用 MAINTENANCE-维护中 RETIRED-已报废',
    description VARCHAR(500) DEFAULT NULL COMMENT '描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_code (code),
    KEY idx_boat_type (boat_type),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='船只表';

-- ========================================
-- 3. 教练排班表 coach_schedule
-- ========================================
DROP TABLE IF EXISTS coach_schedule;
CREATE TABLE coach_schedule (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    coach_id BIGINT NOT NULL COMMENT '教练ID(关联sys_user.id)',
    schedule_date DATE NOT NULL COMMENT '排班日期',
    time_slot VARCHAR(20) NOT NULL COMMENT '时间段：MORNING-上午 AFTERNOON-下午 FULLDAY-全天',
    is_on_duty TINYINT NOT NULL DEFAULT 1 COMMENT '是否值班：0-否 1-是',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_coach_id (coach_id),
    KEY idx_schedule_date (schedule_date),
    KEY idx_coach_date (coach_id, schedule_date),
    CONSTRAINT fk_coach_schedule_coach FOREIGN KEY (coach_id) REFERENCES sys_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教练排班表';

-- ========================================
-- 4. 下水预约表 sailing_booking
-- ========================================
DROP TABLE IF EXISTS sailing_booking;
CREATE TABLE sailing_booking (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    member_id BIGINT NOT NULL COMMENT '会员ID(关联sys_user.id)',
    boat_id BIGINT NOT NULL COMMENT '船只ID(关联boat.id)',
    coach_id BIGINT DEFAULT NULL COMMENT '教练ID(关联sys_user.id)',
    booking_date DATE NOT NULL COMMENT '预约日期',
    time_slot VARCHAR(20) NOT NULL COMMENT '时间段：MORNING-上午 AFTERNOON-下午 FULLDAY-全天',
    booking_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '预约状态：PENDING-待审批 APPROVED-已通过 REJECTED-已拒绝 CANCELLED-已取消 COMPLETED-已完成',
    apply_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    approve_time DATETIME DEFAULT NULL COMMENT '审批时间',
    approve_by BIGINT DEFAULT NULL COMMENT '审批人ID',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_member_id (member_id),
    KEY idx_boat_id (boat_id),
    KEY idx_coach_id (coach_id),
    KEY idx_booking_date (booking_date),
    KEY idx_status (booking_status),
    KEY idx_member_date (member_id, booking_date),
    CONSTRAINT fk_booking_member FOREIGN KEY (member_id) REFERENCES sys_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_boat FOREIGN KEY (boat_id) REFERENCES boat(id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_coach FOREIGN KEY (coach_id) REFERENCES sys_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='下水预约表';

-- ========================================
-- 5. 出海记录表 sailing_record
-- ========================================
DROP TABLE IF EXISTS sailing_record;
CREATE TABLE sailing_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    booking_id BIGINT DEFAULT NULL COMMENT '预约ID(关联sailing_booking.id)',
    boat_id BIGINT NOT NULL COMMENT '船只ID(关联boat.id)',
    coach_id BIGINT DEFAULT NULL COMMENT '教练ID(关联sys_user.id)',
    member_id BIGINT NOT NULL COMMENT '会员ID(关联sys_user.id)',
    sail_date DATE NOT NULL COMMENT '出海日期',
    time_slot VARCHAR(20) NOT NULL COMMENT '时间段：MORNING-上午 AFTERNOON-下午 FULLDAY-全天',
    departure_time DATETIME DEFAULT NULL COMMENT '出发时间',
    return_time DATETIME DEFAULT NULL COMMENT '返回时间',
    weather VARCHAR(100) DEFAULT NULL COMMENT '天气情况',
    wind_speed VARCHAR(50) DEFAULT NULL COMMENT '风速',
    tide VARCHAR(50) DEFAULT NULL COMMENT '潮汐情况',
    duration_minutes INT DEFAULT NULL COMMENT '出海时长(分钟)',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_booking_id (booking_id),
    KEY idx_boat_id (boat_id),
    KEY idx_coach_id (coach_id),
    KEY idx_member_id (member_id),
    KEY idx_sail_date (sail_date),
    CONSTRAINT fk_record_booking FOREIGN KEY (booking_id) REFERENCES sailing_booking(id) ON DELETE SET NULL,
    CONSTRAINT fk_record_boat FOREIGN KEY (boat_id) REFERENCES boat(id) ON DELETE CASCADE,
    CONSTRAINT fk_record_coach FOREIGN KEY (coach_id) REFERENCES sys_user(id) ON DELETE SET NULL,
    CONSTRAINT fk_record_member FOREIGN KEY (member_id) REFERENCES sys_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='出海记录表';

-- ========================================
-- 6. 封场记录表 closure_record
-- ========================================
DROP TABLE IF EXISTS closure_record;
CREATE TABLE closure_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    closure_date DATE NOT NULL COMMENT '封场日期',
    time_slot VARCHAR(20) NOT NULL COMMENT '时间段：MORNING-上午 AFTERNOON-下午 FULLDAY-全天',
    reason VARCHAR(500) NOT NULL COMMENT '封场原因',
    weather_info VARCHAR(500) DEFAULT NULL COMMENT '天气信息',
    created_by BIGINT NOT NULL COMMENT '创建人ID(关联sys_user.id)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-已取消 1-生效中',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_closure_date (closure_date),
    KEY idx_created_by (created_by),
    KEY idx_status (status),
    CONSTRAINT fk_closure_created_by FOREIGN KEY (created_by) REFERENCES sys_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='封场记录表';

-- ========================================
-- 初始化演示数据
-- ========================================

-- BCrypt加密的123456（$2a$10$前缀）
SET @pwd123456 = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy';

-- 插入用户数据
INSERT INTO sys_user (username, password, real_name, phone, role, status) VALUES
('admin', @pwd123456, '系统管理员', '13800000000', 'ADMIN', 1),
('coach1', @pwd123456, '张教练', '13800000001', 'COACH', 1),
('coach2', @pwd123456, '李教练', '13800000002', 'COACH', 1),
('coach3', @pwd123456, '王教练', '13800000003', 'COACH', 1),
('member1', @pwd123456, '陈小明', '13800000011', 'MEMBER', 1),
('member2', @pwd123456, '刘小红', '13800000012', 'MEMBER', 1),
('member3', @pwd123456, '周大海', '13800000013', 'MEMBER', 1),
('member4', @pwd123456, '吴丽华', '13800000014', 'MEMBER', 1),
('member5', @pwd123456, '郑扬帆', '13800000015', 'MEMBER', 1);

-- 插入船只数据
INSERT INTO boat (name, code, boat_type, capacity, status, description) VALUES
('蓝帆一号', 'SAIL-001', 'SAILBOAT', 4, 'AVAILABLE', '竞技型帆船，适合训练和比赛'),
('蓝帆二号', 'SAIL-002', 'SAILBOAT', 6, 'AVAILABLE', '休闲型帆船，适合家庭体验'),
('激流勇士', 'KAYAK-001', 'KAYAK', 2, 'AVAILABLE', '双人皮划艇，静水激流两用'),
('轻舟一号', 'KAYAK-002', 'KAYAK', 1, 'AVAILABLE', '单人皮划艇，适合新手训练'),
('水上漂', 'PAD-001', 'PADDLEBOARD', 1, 'AVAILABLE', '桨板，瑜伽娱乐首选'),
('双体风行者', 'CATA-001', 'CATAMARAN', 8, 'AVAILABLE', '双体帆船，平稳舒适，载客量大');

-- 教练未来7天排班数据
INSERT INTO coach_schedule (coach_id, schedule_date, time_slot, is_on_duty, remark) VALUES
(2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'MORNING', 1, '上午值班'),
(2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'AFTERNOON', 0, '下午休息'),
(3, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'MORNING', 0, '上午休息'),
(3, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'AFTERNOON', 1, '下午值班'),
(4, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'FULLDAY', 1, '全天值班'),

(2, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'FULLDAY', 1, '全天值班'),
(3, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'MORNING', 1, '上午值班'),
(3, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'AFTERNOON', 0, '下午休息'),
(4, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'MORNING', 0, '上午休息'),
(4, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'AFTERNOON', 1, '下午值班'),

(2, DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'MORNING', 0, '上午休息'),
(2, DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'AFTERNOON', 1, '下午值班'),
(3, DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'FULLDAY', 1, '全天值班'),
(4, DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'MORNING', 1, '上午值班'),
(4, DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'AFTERNOON', 0, '下午休息'),

(2, DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'MORNING', 1, '上午值班'),
(2, DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'AFTERNOON', 1, '下午值班'),
(3, DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'MORNING', 0, '轮休'),
(3, DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'AFTERNOON', 0, '轮休'),
(4, DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'FULLDAY', 1, '全天值班'),

(2, DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'FULLDAY', 1, '全天值班'),
(3, DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'MORNING', 1, '上午值班'),
(3, DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'AFTERNOON', 1, '下午值班'),
(4, DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'MORNING', 0, '上午休息'),
(4, DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'AFTERNOON', 0, '下午休息'),

(2, DATE_ADD(CURDATE(), INTERVAL 6 DAY), 'MORNING', 0, '休息'),
(2, DATE_ADD(CURDATE(), INTERVAL 6 DAY), 'AFTERNOON', 0, '休息'),
(3, DATE_ADD(CURDATE(), INTERVAL 6 DAY), 'FULLDAY', 1, '全天值班'),
(4, DATE_ADD(CURDATE(), INTERVAL 6 DAY), 'MORNING', 1, '上午值班'),
(4, DATE_ADD(CURDATE(), INTERVAL 6 DAY), 'AFTERNOON', 1, '下午值班'),

(2, DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'MORNING', 1, '上午值班'),
(2, DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'AFTERNOON', 0, '下午休息'),
(3, DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'MORNING', 0, '上午休息'),
(3, DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'AFTERNOON', 1, '下午值班'),
(4, DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'FULLDAY', 1, '全天值班');

-- 插入预约数据
INSERT INTO sailing_booking (member_id, boat_id, coach_id, booking_date, time_slot, booking_status, apply_time, approve_time, approve_by, remark) VALUES
(5, 1, 2, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 'MORNING', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), 1, '帆船入门训练'),
(6, 3, 3, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 'AFTERNOON', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), 1, '皮划艇体验'),
(7, 6, 4, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'FULLDAY', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), 1, '双体船全家出游'),
(8, 5, NULL, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'MORNING', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), 1, '桨板瑜伽'),
(9, 2, 2, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'AFTERNOON', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), 1, '休闲帆船出海'),
(5, 4, 3, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'MORNING', 'APPROVED', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), 1, '单人皮划艇训练'),
(6, 1, 2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'AFTERNOON', 'APPROVED', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), 1, '进阶帆船训练'),
(7, 3, NULL, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'MORNING', 'PENDING', DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, NULL, '皮划艇初级体验，无需教练'),
(8, 6, 4, DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'FULLDAY', 'PENDING', DATE_SUB(NOW(), INTERVAL 10 HOUR), NULL, NULL, '公司团建活动'),
(9, 5, NULL, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'AFTERNOON', 'REJECTED', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), 1, '当日有大风预警，不适合桨板运动');

-- 插入出海记录（对应已完成的预约）
INSERT INTO sailing_record (booking_id, boat_id, coach_id, member_id, sail_date, time_slot, departure_time, return_time, weather, wind_speed, tide, duration_minutes, remark) VALUES
(1, 1, 2, 5, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 'MORNING', DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 3 DAY), INTERVAL 9 HOUR), DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 3 DAY), INTERVAL 11 HOUR 30 MINUTE), '晴', '东北风3-4级', '高潮', 150, '训练顺利，会员掌握基本操作'),
(2, 3, 3, 6, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 'AFTERNOON', DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 3 DAY), INTERVAL 14 HOUR), DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 3 DAY), INTERVAL 16 HOUR), '多云', '东风2级', '中潮', 120, '静水划行，非常适合新手'),
(3, 6, 4, 7, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'FULLDAY', DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 2 DAY), INTERVAL 9 HOUR 30 MINUTE), DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 2 DAY), INTERVAL 16 HOUR), '晴', '东南风4级', '高潮', 390, '全家出海体验愉快，中途下海游泳'),
(4, 5, NULL, 8, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'MORNING', DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 2 DAY), INTERVAL 8 HOUR), DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 2 DAY), INTERVAL 10 HOUR), '晴', '微风', '低潮', 120, '完成桨板瑜伽课程'),
(5, 2, 2, 9, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'AFTERNOON', DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 1 DAY), INTERVAL 13 HOUR 30 MINUTE), DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 1 DAY), INTERVAL 16 HOUR 30 MINUTE), '晴间多云', '南风3级', '中潮', 180, '休闲出海，钓了几条小鱼');

-- 插入封场记录
INSERT INTO closure_record (closure_date, time_slot, reason, weather_info, created_by, status) VALUES
(DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'FULLDAY', '台风预警，所有水上活动暂停', '预计台风正面登陆，风力10级以上，暴雨', 1, 1),
(DATE_ADD(CURDATE(), INTERVAL 10 DAY), 'MORNING', '船只例行保养维护', '晴天', 1, 1);

SET FOREIGN_KEY_CHECKS = 1;
