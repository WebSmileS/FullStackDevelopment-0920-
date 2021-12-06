package com.yrt.project.modular.advance.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author ：GuangxiZhong
 * @date ：Created in 2021/8/15 15:53
 * @description：
 * @modified By：
 * @version: 1.0
 */
@Data
public class SalesDetailDtoReq {

    @ApiModelProperty(value = "预售单产品明细ID", required = true)
    private Long saleDetailId;
    @ApiModelProperty(value = "本次操作数量", required = true)
    private BigDecimal quantity;
    @ApiModelProperty(value = "税率", required = true)
    private BigDecimal rate;
    @ApiModelProperty(value = "单价", required = true)
    private BigDecimal unitPrice;
}
