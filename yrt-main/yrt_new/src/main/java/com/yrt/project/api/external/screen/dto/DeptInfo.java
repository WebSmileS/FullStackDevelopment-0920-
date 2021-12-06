package com.yrt.project.api.external.screen.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DeptInfo {
    @ApiModelProperty(value = "医院id")
    private Long hospital_inner_sn;

    @ApiModelProperty(value = "部门id")
    private Long department_inner_sn;

    @ApiModelProperty(value = "部门名")
    private String name;

    @ApiModelProperty(value = "部门编号")
    private String department_sn;
}
