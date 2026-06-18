package com.sailing.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class BoatDTO {

    private Long id;

    @NotBlank(message = "船只名称不能为空")
    private String name;

    @NotBlank(message = "船只编号不能为空")
    private String code;

    @NotBlank(message = "船型不能为空")
    private String boatType;

    @NotNull(message = "载客量不能为空")
    private Integer capacity;

    @NotBlank(message = "船只状态不能为空")
    private String status;

    private String description;
}
