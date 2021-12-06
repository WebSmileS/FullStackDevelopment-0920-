package com.yrt.project.modular.inventory.domain;

import com.yrt.common.dict.SystemType;
import lombok.Data;

import java.util.Date;

/**
 * @author ：GuangxiZhong
 * @date ：Created in 2021/8/30 14:59
 * @description：
 * @modified By：
 * @version: 1.0
 */
@Data
public class MinUnitInventory {
    /**
     * 医院内部编号
     */
    private Integer orgId;

    /**
     * 仓库内部编号
     */
    private Integer warehouseInnerSn;

    /**
     * (产品)厂商内部编号
     */
    private Integer productVendorInnerSn;

    /**
     * 产品内部编号
     */
    private Integer productInnerSn;

    /**
     * 型号规格内部编号
     */
    private Short specificationInnerSn;

    /**
     * 批号，空字符串表示无批号
     */
    private String batchNumber;

    /**
     * 单位内部编号
     */
    private Short unitInnerSn;

    /**
     * 数量
     */
    private Long quantity;

    /**
     * 生产日期
     */
    private Date produceDate;

    /**
     * 失效日期
     */
    private Date overdueDate;

    /**
     * 平均单价
     */
    private Long averagePrice;

    /**
     * (产品)厂商名称(冗余)
     */
    private String productVendorName;

    /**
     * 产品名称(冗余)
     */
    private String productName;

    /**
     * 型号规格(冗余)
     */
    private String specification;

    /**
     * 单位(冗余)
     */
    private String unit;

    private SystemType systemType;

    private int version;
}
