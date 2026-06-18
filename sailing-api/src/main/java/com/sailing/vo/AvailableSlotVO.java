package com.sailing.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AvailableSlotVO {

    private LocalDate date;

    private String timeSlot;

    private String timeSlotName;

    private Boolean available;

    private String reason;
}
