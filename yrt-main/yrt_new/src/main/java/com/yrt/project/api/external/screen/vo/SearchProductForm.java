package com.yrt.project.api.external.screen.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchProductForm {
    @ApiModelProperty(value = "产品ID",required = true)
    private Long product_inner_sn;

    @ApiModelProperty(value = "产品生产商ID",required = true)
    private Long vendor_inner_sn;
}
