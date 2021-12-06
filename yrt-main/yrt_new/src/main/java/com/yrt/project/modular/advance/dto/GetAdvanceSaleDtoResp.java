package com.yrt.project.modular.advance.dto;

import com.yrt.common.dict.AdvanceSaleCheckStatusEnum;
import com.yrt.common.dict.AdvanceSaleStatusEnum;
import com.yrt.common.dict.BillsType;
import com.yrt.project.modular.advance.domain.AdvanceSaleDetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ：GuangxiZhong
 * @date ：Created in 2021/8/11 16:52
 * @description：
 * @modified By：
 * @version: 1.0
 */
@Data
public class GetAdvanceSaleDtoResp {

    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 预售单号
     */
    @ApiModelProperty(value = "预售单号")
    private String advanceSaleNo;

    /**
     * 预售方ID
     */
    @ApiModelProperty(value = "预售方ID")
    private Long sellersId;

    /**
     * 预售方名称
     */
    @ApiModelProperty(value = "预售方名称")
    private String sellersName;

    /**
     * 采购方id
     */
    @ApiModelProperty(value = "采购方id")
    private Long buyerId;

    /**
     * 采购方名称
     */
    @ApiModelProperty(value = "采购方名称")
    private String buyerName;

    /**
     * 创建人id
     */
    @ApiModelProperty(value = "创建人id")
    private Long createUserId;

    /**
     * 创建人名称
     */
    @ApiModelProperty(value = "创建人名称")
    private String createUserName;

    /**
     * 审核人名称
     */
    @ApiModelProperty(value = "审核人名称")
    private Long auditUserId;

    /**
     * 审核人名称
     */
    @ApiModelProperty(value = "审核人名称")
    private String auditUserName;

    /**
     * 审核时间
     */
    @ApiModelProperty(value = "审核时间")
    private Date auditTime;

    /**
     * 审核状态，0未审核，1已审核
     */
    @ApiModelProperty(value = "审核状态，UNCHECKED 未审核，APPROVED 已审核")
    private AdvanceSaleCheckStatusEnum auditStatus;

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
    @ApiModelProperty(value = "仓库ID")
    private Long warehouseId;

    /**
     * 仓库名称
     */
    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;

    /**
     * 仓库人员
     */
    @ApiModelProperty(value = "仓库人员")
    private Long warehouseUserId;

    /**
     * 仓库人员名称
     */
    @ApiModelProperty(value = "仓库人员名称")
    private String warehouseUserName;

    /**
     * 经办人
     */
    @ApiModelProperty(value = "经办人")
    private Long agentUserId;

    /**
     * 经办人名称
     */
    @ApiModelProperty(value = "经办人名称")
    private String agentUserName;

    /**
     * 单据类型
     */
    @ApiModelProperty(value = "单据类型")
    private BillsType billsType;

    /**
     * 开单部门ID
     */
    @ApiModelProperty(value = "开单部门ID")
    private Long saleDepartmentId;

    /**
     * 开单部门名称
     */
    @ApiModelProperty(value = "开单部门名称")
    private String saleDepartmentName;

    /**
     * 使用科室
     */
    @ApiModelProperty(value = "使用科室")
    private String useDepartment;

    @ApiModelProperty("销售状态")
    private AdvanceSaleStatusEnum status;

    @ApiModelProperty("明细，只在 /getAdvanceById/{id} 这个接口中有返回")
    private List<AddAdvanceSaleDetailDtoResp> details;

    public static AdvanceSaleStatusEnum getSaleStatusByQuantity(int quantity, int soldQuantity) {
        if (soldQuantity!=quantity){
            return AdvanceSaleStatusEnum.UNDONE;
        }
        return AdvanceSaleStatusEnum.COMPLETED;
    }

    public static AdvanceSaleStatusEnum getSaleStatusByDetails(List<AdvanceSaleDetail> details) {
        List<AdvanceSaleStatusEnum> advanceSaleStatusEnums = details.stream().map(AdvanceSaleDetail::getStatus).collect(Collectors.toList());
        if (advanceSaleStatusEnums.contains(AdvanceSaleStatusEnum.UNDONE)){
            return AdvanceSaleStatusEnum.UNDONE;
        }
        return AdvanceSaleStatusEnum.COMPLETED;
    }
}
