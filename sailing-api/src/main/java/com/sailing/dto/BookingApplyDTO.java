package com.sailing.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class BookingApplyDTO implements Serializable {

    private Long memberId;

    private Long boatId;

    private Long coachId;

    private LocalDate bookingDate;

    private String timeSlot;

    private String remark;
}
