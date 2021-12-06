package com.yrt.project.modular.advance.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yrt.common.dict.AdvanceSaleStatusEnum;
import com.yrt.common.dict.GrnOdoDetailType;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 预售明细表
 */
@Data
@TableName(value = "advance_sale_detail")
public class AdvanceSaleDetail {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 类别0常规，1赠品
     */
    @TableField(value = "`type`")
    private GrnOdoDetailType type;

    /**
     * 批号
     */
    @TableField(value = "batch_number")
    private String batchNumber;

    /**
     * 主表id
     */
    @TableField(value = "advance_sale_id")
    private Long advanceSaleId;

    /**
     * 单位名称
     */
    @TableField(value = "unit_name")
    private String unitName;

    /**
     * 产品厂商id
     */
    @TableField(value = "product_vendor_id")
    private Long productVendorId;

    /**
     * 排序号
     */
    @TableField(value = "sort_number")
    private Integer sortNumber;

    /**
     * 数量
     */
    @TableField(value = "quantity")
    private BigDecimal quantity;

    /**
     * 单位ID
     */
    @TableField(value = "unit_id")
    private Long unitId;

    /**
     * 产品规格型号名称
     */
    @TableField(value = "specification_name")
    private String specificationName;

    /**
     * 产品规格型号ID
     */
    @TableField(value = "specification_id")
    private Long specificationId;

    /**
     * 产品厂商名称
     */
    @TableField(value = "product_vendor_name")
    private String productVendorName;

    /**
     * 产品名称
     */
    @TableField(value = "product_name")
    private String productName;

    /**
     * 产品ID
     */
    @TableField(value = "product_id")
    private Long productId;

    /**
     * 状态 0未完成 1已完成
     */
    @TableField(value = "`status`")
    private AdvanceSaleStatusEnum status;

    /**
     * 产品类型 0:经销商(自建)产品   1:厂商产品
     */
    @TableField(value = "product_type")
    private Integer productType = 1;

    /**
     * 医保编号
     */
    @TableField(value = "`health_care_sn`")
    private String healthCareSn;

    /**
     * 注册证ID
     */
    @TableField(value = "`mdrf_inner_sn`")
    private Long mdrfInnerSn;

    /**
     * 注册证号
     */
    @TableField(value = "`mdrf_sn`")
    private String mdrfSn="";

    /**
     * 已销售数量
     */
    @TableField(value = "`sold_quantity`")
    private Integer soldQuantity = 0;

    /**
     * 已回库数量
     */
    @TableField(value = "`respond_quantity`")
    private Integer respondQuantity = 0;


    public static final String COL_ID = "id";

    public static final String COL_TYPE = "type";

    public static final String COL_BATCH_NUMBER = "batch_number";

    public static final String COL_ADVANCE_SALE_ID = "advance_sale_id";

    public static final String COL_DISCOUNT = "discount";

    public static final String COL_UNIT_PRICE = "unit_price";

    public static final String COL_UNIT_NAME = "unit_name";

    public static final String COL_PRODUCT_VENDOR_ID = "product_vendor_id";

    public static final String COL_SORT_NUMBER = "sort_number";

    public static final String COL_RATE = "rate";

    public static final String COL_QUANTITY = "quantity";

    public static final String COL_UNIT_ID = "unit_id";

    public static final String COL_SPECIFICATION_NAME = "specification_name";

    public static final String COL_SPECIFICATION_ID = "specification_id";

    public static final String COL_PRODUCT_VENDOR_NAME = "product_vendor_name";

    public static final String COL_PRODUCT_NAME = "product_name";

    public static final String COL_PRODUCT_ID = "product_id";

    public static final String COL_PRODUCT_TYPE = "product_type";

    public static final String COL_PRODUCE_DATE = "produce_date";

    public static final String COL_OVERDUE_DATE = "overdue_date";

    public static final String COL_BUYING_PRICE = "buying_price";

    public static final String COL_SOLD_QUANTITY = "sold_quantity";

    public static final String COL_RESPOND_QUANTITY = "respond_quantity";
}