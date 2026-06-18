package com.sailing.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class ClosureRecordDTO implements Serializable {

    private Long id;

    private LocalDate closureDate;

    private String timeSlot;

    private String reason;

    private String weatherInfo;
}
