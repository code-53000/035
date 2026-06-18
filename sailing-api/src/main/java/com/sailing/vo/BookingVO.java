package com.sailing.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookingVO {

    private Long id;

    private Long memberId;

    private Long boatId;

    private Long coachId;

    private LocalDate bookingDate;

    private String timeSlot;

    private String bookingStatus;

    private LocalDateTime applyTime;

    private LocalDateTime approveTime;

    private Long approveBy;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String memberName;

    private String memberPhone;

    private String boatName;

    private String coachName;

    private String statusName;

    private String timeSlotName;
}
