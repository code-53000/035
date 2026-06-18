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

    private String route;

    private String weather;

    private String remark;

    private Long createdBy;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer deleted;

    private String memberName;

    private String memberPhone;

    private String boatName;

    private String coachName;

    private String timeSlotName;
}
