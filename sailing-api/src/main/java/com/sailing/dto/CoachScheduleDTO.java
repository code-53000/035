package com.sailing.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class CoachScheduleDTO implements Serializable {

    private Long id;

    private Long coachId;

    private LocalDate scheduleDate;

    private String timeSlot;

    private Integer isOnDuty;

    private String remark;
}
