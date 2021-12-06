package com.yrt.project.api.external.screen.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductTypeQuantity {
    @ApiModelProperty(value = "产品类型名称")
    private String type;

    @ApiModelProperty(value = "产品类型数量")
    private BigDecimal quantity;
}
