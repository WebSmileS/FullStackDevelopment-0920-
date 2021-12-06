package com.yrt.project.api.external.screen.vo;

import com.yrt.project.api.external.screen.dto.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreenData {

    @ApiModelProperty(value = "产品类型统计，左上")
    List<ProductTypeQuantity> productTypeQuantities;

    @ApiModelProperty(value = "交易统计，左下")
    private ProductTradeData productTradeData;

    @ApiModelProperty(value = "机构统计，中间")
    private OrgData orgData;

    @ApiModelProperty(value = "机构预警，右上")
    private OrgWarnData orgWarnData;

    @ApiModelProperty(value = "产品预警，右下")
    private ProductWarnData productWarnData;

}
