package com.yrt.project.modular.advance.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import com.yrt.common.dict.AdvanceSaleCheckStatusEnum;
import com.yrt.common.dict.BillsType;
import lombok.Data;

/**
 * 预售单表
 */
@Data
@TableName(value = "advance_sale")
public class AdvanceSale {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 预售单号
     */
    @TableField(value = "advance_sale_no")
    private String advanceSaleNo;

    /**
     * 预售方ID
     */
    @TableField(value = "sellers_id")
    private Long sellersId;

    /**
     * 预售方名称
     */
    @TableField(value = "sellers_name")
    private String sellersName;

    /**
     * 采购方id
     */
    @TableField(value = "buyer_id")
    private Long buyerId;

    /**
     * 采购方名称
     */
    @TableField(value = "buyer_name")
    private String buyerName;

    /**
     * 创建人id
     */
    @TableField(value = "create_user_id")
    private Long createUserId;

    /**
     * 创建人名称
     */
    @TableField(value = "create_user_name")
    private String createUserName;

    /**
     * 审核人名称
     */
    @TableField(value = "audit_user_id")
    private Long auditUserId;

    /**
     * 审核人名称
     */
    @TableField(value = "audit_user_name")
    private String auditUserName;

    /**
     * 审核时间
     */
    @TableField(value = "audit_time")
    private Date auditTime;

    /**
     * 审核状态，0未确认，1已确认
     */
    @TableField(value = "audit_status")
    private AdvanceSaleCheckStatusEnum auditStatus;

    /**
     * 描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 期望到货日期
     */
    @TableField(value = "expect_aog_date")
    private Date expectAogDate;

    /**
     * 仓库ID
     */
    @TableField(value = "warehouse_id")
    private Long warehouseId;

    /**
     * 仓库名称
     */
    @TableField(value = "warehouse_name")
    private String warehouseName;

    /**
     * 仓库人员
     */
    @TableField(value = "warehouse_user_id")
    private Long warehouseUserId;

    /**
     * 仓库人员名称
     */
    @TableField(value = "warehouse_user_name")
    private String warehouseUserName;

    /**
     * 经办人
     */
    @TableField(value = "agent_user_id")
    private Long agentUserId;

    /**
     * 经办人名称
     */
    @TableField(value = "agent_user_name")
    private String agentUserName;

    /**
     * 单据类型
     */
    @TableField(value = "bills_type")
    private BillsType billsType;

    /**
     * 开单部门ID
     */
    @TableField(value = "sale_department_id")
    private Long saleDepartmentId;

    /**
     * 开单部门名称
     */
    @TableField(value = "sale_department_name")
    private String saleDepartmentName;

    /**
     * 使用科室
     */
    @TableField(value = "use_department")
    private String useDepartment;

    public static final String COL_ID = "id";

    public static final String COL_ADVANCE_SALE_NO = "advance_sale_no";

    public static final String COL_SELLERS_ID = "sellers_id";

    public static final String COL_SELLERS_NAME = "sellers_name";

    public static final String COL_SELLERS_TYPE = "sellers_type";

    public static final String COL_BUYER_ID = "buyer_id";

    public static final String COL_BUYER_NAME = "buyer_name";

    public static final String COL_BUYER_TYPE = "buyer_type";

    public static final String COL_CREATE_USER_ID = "create_user_id";

    public static final String COL_CREATE_USER_NAME = "create_user_name";

    public static final String COL_AUDIT_USER_ID = "audit_user_id";

    public static final String COL_AUDIT_USER_NAME = "audit_user_name";

    public static final String COL_AUDIT_TIME = "audit_time";

    public static final String COL_AUDIT_STATUS = "audit_status";

    public static final String COL_DESCRIPTION = "description";

    public static final String COL_EXPECT_AOG_DATE = "expect_aog_date";

    public static final String COL_STATUS = "status";

    public static final String COL_WAREHOUSE_ID = "warehouse_id";

    public static final String COL_WAREHOUSE_NAME = "warehouse_name";

    public static final String COL_WAREHOUSE_USER_ID = "warehouse_user_id";

    public static final String COL_WAREHOUSE_USER_NAME = "warehouse_user_name";

    public static final String COL_AGENT_USER_ID = "agent_user_id";

    public static final String COL_AGENT_USER_NAME = "agent_user_name";
}