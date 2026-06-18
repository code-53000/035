package com.sailing.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingQueryDTO {

    private Long memberId;

    private Long coachId;

    private Long boatId;

    private String status;

    private String bookingStatus;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer page;

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    public Integer getPageNum() {
        if (page != null) return page;
        return pageNum;
    }

    public String getBookingStatus() {
        if (bookingStatus != null) return bookingStatus;
        return status;
    }
}
