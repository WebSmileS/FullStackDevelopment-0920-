package com.yrt.project.api.external.screen.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrgWarn {
    @ApiModelProperty(value = "机构类型 1-医院 2-经销商 3-厂商 4-回收商 5-科室")
    private int type;

    @ApiModelProperty(value = "机构类型的预警数")
    private BigDecimal count;
}
