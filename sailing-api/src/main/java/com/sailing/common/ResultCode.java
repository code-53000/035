package com.sailing.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),

    USER_NOT_FOUND(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "密码错误"),
    USER_USERNAME_EXIST(1003, "用户名已存在"),
    USER_PHONE_EXIST(1004, "手机号已存在"),
    USER_DISABLED(1005, "用户已禁用"),

    TOKEN_INVALID(2001, "Token无效"),
    TOKEN_EXPIRED(2002, "Token已过期"),
    TOKEN_EMPTY(2003, "Token为空"),

    BOAT_NOT_FOUND(3001, "船只不存在"),
    BOAT_NOT_AVAILABLE(3002, "船只不可用"),

    BOOKING_NOT_FOUND(4001, "预约不存在"),
    BOOKING_STATUS_ERROR(4002, "预约状态错误"),
    BOOKING_TIME_CONFLICT(4003, "预约时间冲突"),

    COACH_NOT_FOUND(5001, "教练不存在"),
    COACH_SCHEDULE_CONFLICT(5002, "教练时间冲突");

    private final Integer code;
    private final String message;
}
