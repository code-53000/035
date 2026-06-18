package com.sailing.dto;

import lombok.Data;

@Data
public class LoginRespDTO {

    private String token;

    private UserInfoDTO userInfo;

    private UserInfoDTO user;

    private Long userId;

    private String username;

    private String realName;

    private String role;

    private String phone;
}
