package com.yrt.project.api.external.screen.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductTradeInfo {
    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "机构名称")
    private String orgName;

    @ApiModelProperty(value = "交易数量")
    private BigDecimal quantity;

    @ApiModelProperty(value = "计数单位")
    private String unit_name;

    @ApiModelProperty(value = "交易时刻")
    private String at;

    private String batch_number;

    @ApiModelProperty(value = "规格型号")
    private String specification;

    @ApiModelProperty(value = "生产企业")
    private String producer_org;

    @ApiModelProperty(value = "销售企业")
    private String sale_org;

    @ApiModelProperty(value = "入库时间")
    private String in_time;

    @ApiModelProperty(value = "产品厂商id")
    private long vd_inner_sn;

    @ApiModelProperty(value = "产品id")
    private long product_inner_sn;
}
