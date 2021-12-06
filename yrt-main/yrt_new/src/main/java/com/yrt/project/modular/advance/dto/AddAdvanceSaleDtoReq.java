package com.yrt.project.modular.advance.dto;

import com.yrt.common.dict.BillsType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @author ：GuangxiZhong
 * @date ：Created in 2021/8/11 16:52
 * @description：
 * @modified By：
 * @version: 1.0
 */
@Data
public class AddAdvanceSaleDtoReq {

    @ApiModelProperty("预售单ID，修改预售单时需要传此值，新增时不传")
    private Long Id;

    /**
     * 采购方id
     */
    @ApiModelProperty(value = "采购方ID", required = true)
    @NotNull(message = "采购方ID不能为空")
    private Long buyerId;

    /**
     * 采购方名称名称
     */
    @ApiModelProperty(value = "采购方名称", required = true)
    @NotNull(message = "采购方名称不能为空")
    private String buyerName;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * 期望到货日期
     */
    @ApiModelProperty(value = "期望到货日期")
    private Date expectAogDate;

    /**
     * 仓库ID
     */
    @ApiModelProperty(value = "仓库ID", required = true)
    @NotNull(message = "仓库ID不能为空")
    private Long warehouseId;

    /**
     * 仓库名称
     */
    @ApiModelProperty(value = "仓库名称", required = true)
    @NotNull(message = "仓库名称不能为空")
    private String warehouseName;

    /**
     * 仓库人员ID
     */
    @ApiModelProperty(value = "仓库人员ID", required = true)
    private Long warehouseUserId;

    /**
     * 仓库人员名称
     */
    @ApiModelProperty(value = "仓库人员名称", required = true)
    private String warehouseUserName="";

    /**
     * 经办人
     */
    @ApiModelProperty(value = "经办人ID", required = true)
    @NotNull(message = "经办人名称ID不能为空")
    private Long agentUserId;

    /**
     * 经办人名称
     */
    @ApiModelProperty(value = "经办人名称", required = true)
    @NotNull(message = "经办人名称不能为空")
    private String agentUserName;

    @ApiModelProperty(value="单据类型",required=true)
    @NotNull(message = "单据类型不能为空")
    private BillsType billsType;

    /**
     * 开单部门ID
     */
    @ApiModelProperty(value="开单部门ID",required=true)
    @NotNull(message = "开单部门ID不能为空")
    private Long saleDepartmentId;

    /**
     * 开单部门名称
     */
    @ApiModelProperty(value="开单部门名称",required=true)
    @NotNull(message = "开单部门名称不能为空")
    private String saleDepartmentName;

    /**
     * 预售单产品明细
     */
    @ApiModelProperty(value = "预售单产品明细", required = true)
    @Valid
    @NotEmpty(message = "预售单产品明细不能为空")
    private List<AddAdvanceSaleDetailDtoReq> details;


}
