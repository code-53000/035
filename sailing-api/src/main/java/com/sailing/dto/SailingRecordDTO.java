package com.sailing.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SailingRecordDTO implements Serializable {

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
}
