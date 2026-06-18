package com.sailing.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoatVO {

    private Long id;

    private String name;

    private String code;

    private String boatType;

    private Integer capacity;

    private String status;

    private String description;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer deleted;

    private String boatTypeName;

    private String statusName;
}
