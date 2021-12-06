package com.yrt.project.modular.premium.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
    * 溢价规则管理
    */
@Data
@TableName(value = "premium_rule")
public class PremiumRule {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 机构ID
     */
    @TableField(value = "org_id")
    private Long orgId;

    /**
     * 机构名称
     */
    @TableField(value = "org_name")
    private String orgName;

    /**
     * 最低价
     */
    @TableField(value = "min_price")
    private BigDecimal minPrice;

    /**
     * 最高价
     */
    @TableField(value = "max_price")
    private BigDecimal maxPrice;

    /**
     * 溢价比例
     */
    @TableField(value = "`percent`")
    private BigDecimal percent;

    /**
     * 备注
     */
    @TableField(value = "remake")
    private String remake;

    /**
     * 操作人id
     */
    @TableField(value = "update_user_id")
    private Long updateUserId;

    /**
     * 操作人名称
     */
    @TableField(value = "update_user_name")
    private String updateUserName;

    public static final String COL_ID = "id";

    public static final String COL_ORG_ID = "org_id";

    public static final String COL_ORG_NAME = "org_name";

    public static final String COL_MIN_PRICE = "min_price";

    public static final String COL_MAX_PRICE = "max_price";

    public static final String COL_PERCENT = "percent";

    public static final String COL_REMAKE = "remake";

    public static final String COL_UPDATE_USER_ID = "update_user_id";

    public static final String COL_UPDATE_USER_NAME = "update_user_name";
}