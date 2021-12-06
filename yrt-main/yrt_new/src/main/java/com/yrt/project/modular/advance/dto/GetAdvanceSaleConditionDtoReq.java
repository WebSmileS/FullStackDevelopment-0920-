package com.yrt.project.modular.advance.dto;

import com.yrt.common.dict.AdvanceSaleCheckStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ：GuangxiZhong
 * @date ：Created in 2021/8/11 16:52
 * @description：
 * @modified By：
 * @version: 1.0
 */
@Data
public class GetAdvanceSaleConditionDtoReq {

    @ApiModelProperty(value = "单据号")
    private String advanceSaleNo;

    /**
     * 采购方id
     */
    @ApiModelProperty(value = "采购方ID")
    private Long buyerId;

    /**
     * 采购方名称名称
     */
    @ApiModelProperty(value = "采购方名称")
    private String buyerName;

    /**
     * 审核状态
     */
    @ApiModelProperty(value = "审核状态，    UNCHECKED(0, \"未确认\"),\n" +
            "    APPROVED(1, \"已确认\");")
    private AdvanceSaleCheckStatusEnum auditStatus;
}
