package com.sailing.dto;

import lombok.Data;

@Data
public class UserInfoDTO {

    private Long id;

    private String username;

    private String realName;

    private String phone;

    private String role;

    private Integer status;
}
