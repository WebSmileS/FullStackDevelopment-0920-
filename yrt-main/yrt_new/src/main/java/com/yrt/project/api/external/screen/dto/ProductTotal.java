package com.yrt.project.api.external.screen.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductTotal {
    @ApiModelProperty(value = "销售金额")
    private BigDecimal saleAmount;

    @ApiModelProperty(value = "合计批次")
    private long batchNumberCount;

    @ApiModelProperty(value = "产品的经销商总数")
    private long dealerCount;

    @ApiModelProperty(value = "产品的使用科室总数")
    private long useDeptCount;

    @ApiModelProperty(value = "产品图片")
    private String imgUrl;
}
