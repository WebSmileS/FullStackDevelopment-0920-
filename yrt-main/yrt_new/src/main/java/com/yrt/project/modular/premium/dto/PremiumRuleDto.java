package com.yrt.project.modular.premium.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author ：GuangxiZhong
 * @date ：Created in 2021/8/17 17:42
 * @description：
 * @modified By：
 * @version: 1.0
 */
@Data
@Accessors(chain = true)
public class PremiumRuleDto {

    @ApiModelProperty(value = "最低价,更新时必传")
    private Long id;

    /**
     * 最低价
     */
    @ApiModelProperty(value = "最低价", required = true)
    @NotNull(message = "最低价不能为空")
    private BigDecimal minPrice;

    /**
     * 最高价
     */
    @ApiModelProperty(value = "最低价", required = true)
    @NotNull(message = "最高价不能为空")
    private BigDecimal maxPrice;

    /**
     * 溢价比例
     */
    @ApiModelProperty(value = "溢价比例", required = true)
    @NotNull(message = "溢价比例不能为空")
    private BigDecimal percent;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remake;
}
