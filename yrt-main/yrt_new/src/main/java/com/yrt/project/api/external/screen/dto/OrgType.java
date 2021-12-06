package com.yrt.project.api.external.screen.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrgType {
    @ApiModelProperty(value = "机构类型名称")
    private int type;

    @ApiModelProperty(value = "机构类型数量")
    private BigDecimal count;

}
