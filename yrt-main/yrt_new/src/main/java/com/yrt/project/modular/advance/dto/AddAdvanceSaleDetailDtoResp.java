package com.yrt.project.modular.advance.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.yrt.common.dict.AdvanceSaleStatusEnum;
import com.yrt.common.dict.GrnOdoDetailType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author ：GuangxiZhong
 * @date ：Created in 2021/8/11 17:24
 * @description：预售单明细DTO
 * @modified By：
 * @version: 1.0
 */
@Data
@Accessors(chain = true)
public class AddAdvanceSaleDetailDtoResp {

    @ApiModelProperty(value = "明细ID", required = true)
    private Long id;

    /**
     * 产品ID
     */
    @ApiModelProperty(value = "产品ID", required = true)
    private Long productId;

    /**
     * 产品名称
     */
    @ApiModelProperty(value = "产品名称", required = true)
    private String productName;

    /**
     * 产品厂商ID
     */
    @ApiModelProperty(value = "产品厂商ID", required = true)
    private Long productVendorId;

    /**
     * 产品厂商名称
     */
    @ApiModelProperty(value = "产品厂商名称", required = true)
    private String productVendorName;

    /**
     * 产品规格型号ID
     */
    @ApiModelProperty(value = "产品规格型号ID", required = true)
    private Long specificationId;

    /**
     * 产品规格型号名称
     */
    @ApiModelProperty(value = "产品规格型号名称", required = true)
    private String specificationName;

    /**
     * 数量
     */
    @ApiModelProperty(value = "总数量", required = true)
    private BigDecimal quantity;

    /**
     * 单位ID
     */
    @ApiModelProperty(value = "单位ID", required = true)
    private Long unitId;


    /**
     * 单位名称
     */
    @ApiModelProperty(value = "单位名称", required = true)
    private String unitName;
    /**
     * 单价
     */
    @ApiModelProperty(value = "单价", required = true)
    private BigDecimal unitPrice;

    /**
     * 折扣
     */
    @ApiModelProperty(value = "折扣", required = true)
    private BigDecimal discount;

    /**
     * 税率
     */
    @ApiModelProperty(value = "税率", required = true)
    private BigDecimal rate;

    /**
     * 排序号
     */
    @ApiModelProperty(value = "排序号", required = true)
    private Integer sortNumber;

    /**
     * 批号
     */
    @ApiModelProperty(value = "批号", required = true)
    private String batchNumber;

    @ApiModelProperty(value = "type", reference = "NORMAL: 常规，GIFT：赠品")
    private GrnOdoDetailType type;

    /**
     * 医保编号
     */
    @ApiModelProperty(value="医保编号")
    private String healthCareSn;

    /**
     * 注册证ID
     */
    @ApiModelProperty(value="注册证ID")
    private Long mdrfInnerSn;

    /**
     * 注册证号
     */
    @ApiModelProperty(value="注册证号")
    private String mdrfSn;

    /**
     * 产品类型 0:经销商(自建)产品   1:厂商产品
     */
    @ApiModelProperty(value = "product_type")
    private Integer productType = 1;

    /**
     * 已销售数量
     */
    @TableField(value = "`sold_quantity`")
    private Integer soldQuantity = 0;

    /**
     * 已回库数量
     */
    @TableField(value = "`respond_quantity`")
    private Integer respondQuantity =0;

    @ApiModelProperty("完成状态")
    private AdvanceSaleStatusEnum status;
}
