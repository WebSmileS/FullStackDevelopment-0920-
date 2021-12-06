package com.yrt.project.modular.premium.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
    * 产品溢价比例表
    */
@Data
@TableName(value = "product_premium_percent")
public class ProductPremiumPercent {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 产品ID
     */
    @TableField(value = "product_id")
    private Long productId;

    /**
     * 产品名称
     */
    @TableField(value = "product_name")
    private String productName;

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
     * 产品厂商IO
     */
    @TableField(value = "product_vendor_id")
    private Long productVendorId;

    /**
     * 产品厂商名称
     */
    @TableField(value = "product_vendor_name")
    private String productVendorName;

    /**
     * 批号
     */
    @TableField(value = "batch_number")
    private String batchNumber;

    /**
     * 单价，成本价
     */
    @TableField(value = "price")
    private BigDecimal price;

    /**
     * 包装单位编码
     */
    @TableField(value = "unit_inner_sn")
    private Long unitInnerSn;

    /**
     * 溢价比例
     */
    @TableField(value = "`percent`")
    private BigDecimal percent;

    /**
     * 售价
     */
    @TableField(value = "sales_price")
    private BigDecimal salesPrice;

    /**
     * 机构ID
     */
    @TableField(value = "org_id")
    private Long orgId;

    /**
     * 最后更新人ID
     */
    @TableField(value = "update_user_id")
    private Long updateUserId;

    public static final String COL_ID = "id";

    public static final String COL_PRODUCT_ID = "product_id";

    public static final String COL_PRODUCT_NAME = "product_name";

    public static final String COL_SPECIFICATION_NAME = "specification_name";

    public static final String COL_SPECIFICATION_ID = "specification_id";

    public static final String COL_PRODUCT_VENDOR_ID = "product_vendor_id";

    public static final String COL_PRODUCT_VENDOR_NAME = "product_vendor_name";

    public static final String COL_BATCH_NUMBER = "batch_number";

    public static final String COL_PRICE = "price";

    public static final String COL_ORG_ID = "org_id";

    public static final String COL_UPDATE_USER_ID = "update_user_id";

    public static final String COL_UNIT_INNER_SN = "unit_inner_sn";
}