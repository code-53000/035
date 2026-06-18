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
@TableName("coach_schedule")
public class CoachSchedule implements Serializable {

    public static final String TIME_SLOT_MORNING = "MORNING";
    public static final String TIME_SLOT_AFTERNOON = "AFTERNOON";
    public static final String TIME_SLOT_FULLDAY = "FULLDAY";

    public static final Integer ON_DUTY_YES = 1;
    public static final Integer ON_DUTY_NO = 0;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long coachId;

    private LocalDate scheduleDate;

    private String timeSlot;

    private Integer isOnDuty;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
