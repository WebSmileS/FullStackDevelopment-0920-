package com.yrt.project.api.external.screen.vo;

import com.yrt.project.api.external.screen.dto.ProductTradeInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductTradeData {
    @ApiModelProperty(value = "日交易数据")
    private List<TradeData> dayTrade;

    @ApiModelProperty(value = "月交易数据")
    private List<TradeData> monthTrade;

    @ApiModelProperty(value = "产品时段交易数据")
    private List<ProductTradeInfo> items;

}
