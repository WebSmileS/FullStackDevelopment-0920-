package com.yrt.project.modular.inventory.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;

/**
    * 医院产品最小单位库存
    */
@Data
@TableName(value = "hospital_product_min_unit_inventory")
@Accessors(chain = true)
public class HospitalProductMinUnitInventory {
    /**
     * 医院内部编号
     */
    @TableField(value = "hospital_inner_sn")
    private Integer hospitalInnerSn;

    /**
     * 仓库内部编号
     */
    @TableField(value = "warehouse_inner_sn")
    private Integer warehouseInnerSn;

    /**
     * (产品)厂商内部编号
     */
    @TableField(value = "product_vendor_inner_sn")
    private Integer productVendorInnerSn;

    /**
     * 产品内部编号
     */
    @TableField(value = "product_inner_sn")
    private Integer productInnerSn;

    /**
     * 型号规格内部编号
     */
    @TableField(value = "specification_inner_sn")
    private Short specificationInnerSn;

    /**
     * 批号，空字符串表示无批号
     */
    @TableField(value = "batch_number")
    private String batchNumber;

    /**
     * 单位内部编号
     */
    @TableField(value = "unit_inner_sn")
    private Short unitInnerSn;

    /**
     * 数量
     */
    @TableField(value = "quantity")
    private Long quantity;

    /**
     * 生产日期
     */
    @TableField(value = "produce_date")
    private Date produceDate;

    /**
     * 失效日期
     */
    @TableField(value = "overdue_date")
    private Date overdueDate;

    /**
     * 平均单价
     */
    @TableField(value = "average_price")
    private Long averagePrice;

    /**
     * (产品)厂商名称(冗余)
     */
    @TableField(value = "product_vendor_name")
    private String productVendorName;

    /**
     * 产品名称(冗余)
     */
    @TableField(value = "product_name")
    private String productName;

    /**
     * 型号规格(冗余)
     */
    @TableField(value = "specification")
    private String specification;

    /**
     * 单位(冗余)
     */
    @TableField(value = "unit")
    private String unit;

    @Version
    private Integer version;

    public static final String COL_HOSPITAL_INNER_SN = "hospital_inner_sn";

    public static final String COL_WAREHOUSE_INNER_SN = "warehouse_inner_sn";

    public static final String COL_PRODUCT_VENDOR_INNER_SN = "product_vendor_inner_sn";

    public static final String COL_PRODUCT_INNER_SN = "product_inner_sn";

    public static final String COL_SPECIFICATION_INNER_SN = "specification_inner_sn";

    public static final String COL_BATCH_NUMBER = "batch_number";

    public static final String COL_UNIT_INNER_SN = "unit_inner_sn";

    public static final String COL_QUANTITY = "quantity";

    public static final String COL_PRODUCE_DATE = "produce_date";

    public static final String COL_OVERDUE_DATE = "overdue_date";

    public static final String COL_AVERAGE_PRICE = "average_price";

    public static final String COL_PRODUCT_VENDOR_NAME = "product_vendor_name";

    public static final String COL_PRODUCT_NAME = "product_name";

    public static final String COL_SPECIFICATION = "specification";

    public static final String COL_UNIT = "unit";
}