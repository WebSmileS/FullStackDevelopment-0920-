package com.yrt.project.api.external.screen.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrgCount {
    @ApiModelProperty(value = "机构类型数量")
    private BigDecimal count;

    @ApiModelProperty(value = "机构归属地")
    private String area;

    @ApiModelProperty(value = "归属地行政区划代码")
    private Long administrative_division_sn;
}
