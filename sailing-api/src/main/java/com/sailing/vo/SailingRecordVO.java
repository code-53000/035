package com.sailing.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SailingRecordVO {

    private Long id;

    private Long bookingId;

    private Long boatId;

    private Long coachId;

    private Long memberId;

    private LocalDate sailDate;

    private String timeSlot;

    private LocalDateTime departureTime;

    private LocalDateTime returnTime;

    private String weather;

    private String windSpeed;

    private String tide;

    private Integer durationMinutes;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String memberName;

    private String boatName;

    private String coachName;

    private String timeSlotName;
}
