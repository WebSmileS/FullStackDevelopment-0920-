package com.yrt.project.api.external.screen.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchWarnForm {
    @ApiModelProperty(value = "省行政区划代码")
    private Long administrative_division_sn_province;

    @ApiModelProperty(value = "市行政区划代码")
    private Long administrative_division_sn_city;

    @ApiModelProperty(value = "查询内容")
    private String content;

    @ApiModelProperty(value = "产品预警类型 0=产品注册证过期预警+产品过期预警 1=产品注册证过期预警 2=产品过期预警")
    private Integer type=0;
}
