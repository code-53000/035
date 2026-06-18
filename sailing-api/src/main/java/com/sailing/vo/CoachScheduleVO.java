package com.sailing.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CoachScheduleVO {

    private Long id;

    private Long coachId;

    private LocalDate scheduleDate;

    private String timeSlot;

    private Integer isOnDuty;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer deleted;

    private String coachName;

    private String timeSlotName;

    private String onDutyName;
}
