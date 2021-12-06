package com.yrt.project.modular.external.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yrt.common.dict.VoucherType;
import lombok.Data;

/**
 * HIS接口
 */
@Data
@TableName(value = "his_interface")
public class HisInterface {
    private static final long serialVersionUID = 1L;
    /**
     * 内部编号
     */
    @TableId(value = "inner_sn", type = IdType.ASSIGN_ID)
    private Long innerSn;

    /**
     * 机构内部编号
     */
    @TableField(value = "organization_inner_sn")
    private Integer organizationInnerSn;

    /**
     * 机构类别
     */
    @TableField(value = "organization_type")
    private Integer organizationType;

    /**
     * 凭证类型
     */
    @TableField(value = "voucher_type")
    private VoucherType voucherType;

    /**
     * url
     */
    @TableField(value = "url")
    private String url;

    public static final String COL_INNER_SN = "inner_sn";

    public static final String COL_ORGANIZATION_INNER_SN = "organization_inner_sn";

    public static final String COL_ORGANIZATION_TYPE = "organization_type";

    public static final String COL_VOUCHER_TYPE = "voucher_type";

    public static final String COL_URL = "url";
}