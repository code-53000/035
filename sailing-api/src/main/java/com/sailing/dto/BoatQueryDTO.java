package com.sailing.dto;

import lombok.Data;

@Data
public class BoatQueryDTO {

    private String keyword;

    private String name;

    private String code;

    private String boatType;

    private String status;

    private Integer page;

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    public Integer getPageNum() {
        if (page != null) return page;
        return pageNum;
    }
}
