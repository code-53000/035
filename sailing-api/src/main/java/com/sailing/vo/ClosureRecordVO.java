package com.sailing.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ClosureRecordVO {

    private Long id;

    private LocalDate closureDate;

    private String timeSlot;

    private String reason;

    private String weatherInfo;

    private Long createdBy;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String createdByName;

    private String timeSlotName;

    private String statusName;
}
