package com.sailing.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("sailing_record")
public class SailingRecord implements Serializable {

    @TableId(type = IdType.AUTO)
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

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
