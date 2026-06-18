package com.sailing.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("closure_record")
public class ClosureRecord implements Serializable {

    public static final Integer STATUS_ACTIVE = 1;
    public static final Integer STATUS_CANCELLED = 0;

    public static final String TIME_SLOT_MORNING = "MORNING";
    public static final String TIME_SLOT_AFTERNOON = "AFTERNOON";
    public static final String TIME_SLOT_FULLDAY = "FULLDAY";

    @TableId(type = IdType.AUTO)
    private Long id;

    private LocalDate closureDate;

    private String timeSlot;

    private String reason;

    private String weatherInfo;

    private Long createdBy;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
