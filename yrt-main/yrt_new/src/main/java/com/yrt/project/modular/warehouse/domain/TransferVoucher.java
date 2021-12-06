package com.yrt.project.modular.warehouse.domain;

import java.io.Serializable;

import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TransferVoucher implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "当前机构内部编号")
	private Long org_id;
	@ApiModelProperty(value = "相关机构内部编号")
	private Long organization_inner_sn;
	@ApiModelProperty(value = "相关机构名称")
	private String organization_name;
	@ApiModelProperty(value = "经手人名称")
	private String handler_name;
	@ApiModelProperty(value = "部门名称")
	private String department_name;
	@ApiModelProperty(value = "调单编号")
	private String voucher_sn;
	@ApiModelProperty(value = "销售单关联单据")
	private Long voucher_inner_sn_c;
	@ApiModelProperty(value = "描述")
	private String description;
	@ApiModelProperty(value="单据类型",required=true)
	private BillsType billsType;
	
	@ApiModelProperty(value = "收货仓库ID")
	private Long warehouse_inner_sn;
	@ApiModelProperty(value = "收货仓库名称")
	private String warehouse_name;
	@ApiModelProperty(value = "收货地址")
	private String delivery_address;
	@ApiModelProperty(value = "使用科室")
	private String use_department;

}
