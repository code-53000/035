package com.sailing.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class BookingQueryDTO implements Serializable {

    private Long memberId;

    private Long coachId;

    private Long boatId;

    private String bookingStatus;

    private LocalDate startDate;

    private LocalDate endDate;
}
