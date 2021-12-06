package com.yrt.project.modular.warehouse.domain;

import java.io.Serializable;

import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TransferSaleRecord implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "当前机构内部编号")
	private Long org_id;
	@ApiModelProperty(value = "相关机构内部编号")
	private Long organization_inner_sn;
	@ApiModelProperty(value = "相关机构名称")
	private String organization_name;
	@ApiModelProperty(value = "销售单内部编号")
	private Long sl_inner_sn;
	@ApiModelProperty(value = "销售员内部编号")
	private Long seller_inner_sn;
	@ApiModelProperty(value = "销售员名称")
	private String seller_name;
	@ApiModelProperty(value = "部门内部编号")
	private Long department_inner_sn;
	@ApiModelProperty(value = "部门名称")
	private String department_name;
	private String auditor_name;
	@ApiModelProperty(value = "状态 1-正式 2-已审核 ")
	private Integer status;
	@ApiModelProperty(value = "销售单编号")
	private String sl_sn;
	@ApiModelProperty(value = "销售单关联单据")
	private Long voucher_inner_sn_c;
	@ApiModelProperty(value = "销售方联系人")
	private String seller_linkman;
	@ApiModelProperty(value = "销售方联系电话")
	private String seller_phone;
	@ApiModelProperty(value = "描述")
	private String description;
	@ApiModelProperty(value="单据类型",required=true)
	private BillsType billsType;
}
