package com.sailing.dto;

import lombok.Data;

@Data
public class BoatQueryDTO {

    private String name;

    private String code;

    private String boatType;

    private String status;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
