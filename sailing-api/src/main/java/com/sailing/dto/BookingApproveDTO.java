package com.sailing.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BookingApproveDTO implements Serializable {

    private Long id;

    private String bookingStatus;

    private String remark;
}
