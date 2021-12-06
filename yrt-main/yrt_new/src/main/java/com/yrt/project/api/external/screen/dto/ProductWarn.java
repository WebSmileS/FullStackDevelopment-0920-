package com.yrt.project.api.external.screen.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProductWarn {
    @ApiModelProperty(value = "产品预警类型")
    private String warnType;

    @ApiModelProperty(value = "预警数量")
    private long count;
}
