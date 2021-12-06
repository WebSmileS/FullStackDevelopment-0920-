package com.yrt.project.api.external.screen.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductTrade {
    @ApiModelProperty(value = "产品分类")
    private String type;

    @ApiModelProperty(value = "交易金额")
    private Long amount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "日期")
    private String date;

    private Long vd_inner_sn;
    private Long product_inner_sn;
    private Long specification_inner_sn;
    private Long unit_inner_sn;
    private BigDecimal quantity;
    private Long unit_price;
}
