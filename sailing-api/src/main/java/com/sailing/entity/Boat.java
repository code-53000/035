package com.sailing.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("boat")
public class Boat implements Serializable {

    public static final String STATUS_AVAILABLE = "AVAILABLE";
    public static final String STATUS_MAINTENANCE = "MAINTENANCE";
    public static final String STATUS_RETIRED = "RETIRED";

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String code;

    private String boatType;

    private Integer capacity;

    private String status;

    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
