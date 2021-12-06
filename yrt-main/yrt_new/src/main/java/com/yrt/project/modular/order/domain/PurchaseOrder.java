package com.yrt.project.modular.order.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PurchaseOrder implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "采购方机构编号")
	private Long partA;
	@ApiModelProperty(value = "采购方机构名称")
	private String partA_name;
	@ApiModelProperty(value = "销售方机构编号")
	private Long partB;
	@ApiModelProperty(value = "销售方机构名称")
	private String partB_name;
	@ApiModelProperty(value = "订单内部编号")
	private Long po_so_inner_sn;
	@ApiModelProperty(value = "采购员内部编号")
	private Long buyer_inner_sn;
	@ApiModelProperty(value = "采采购员名称")
	private String buyer_name;
	@ApiModelProperty(value = "部门内部编号")
	private Long department_inner_sn;
	@ApiModelProperty(value = "部门名称")
	private String department_name;
	@ApiModelProperty(value = "制单人内部编号")
	private Long creator_id;
	@ApiModelProperty(value = "制单人名称")
	private String creator_name;
	@ApiModelProperty(value = "审核人内部编号")
	private Long auditor_id;
	@ApiModelProperty(value = "审核人名称")
	private String auditor_name;
	@ApiModelProperty(value = "单据确认人编号")
	private Long affirmant_inner_sn;
	@ApiModelProperty(value = "单据确认人名称 ")
	private String affirmant_name;
	@ApiModelProperty(value = "采购订单编号")
	private String po_sn;
	@ApiModelProperty(value = "销售订单编号")
	private String so_sn;
	@ApiModelProperty(value = "医院别名")
	private String alias;
	@ApiModelProperty(value = "期望到货日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date expect_aog_date;
	@ApiModelProperty(value = "状态 0-草稿 1-正式 2-已审批 3-确认 4-已终止 5-已完结")
	private Integer status;
	@ApiModelProperty(value = "单据生成方 0-采购 1-销售")
	private Integer create_organization_type;
	@ApiModelProperty(value = "单据生成时间  指正式单据的生成时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date create_time;
	@ApiModelProperty(value = "单据审核时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date audit_time;
	@ApiModelProperty(value = "单据确认时间 ")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date confirm_time;
	@ApiModelProperty(value = "描述")
	private String description;
	@ApiModelProperty(value = "收货仓库ID")
	private Long warehouse_inner_sn;
	@ApiModelProperty(value = "收货仓库名称")
	private String warehouse_name;
	@ApiModelProperty(value = "收货地址")
	private String delivery_address;
	@ApiModelProperty(value="单据类型",required=true)
	private BillsType billsType;

	@ApiModelProperty(value = "供货商开户银行")
	private String b_bank_name;
	@ApiModelProperty(value = "供货商银行账户")
	private String b_bank_account;

	@ApiModelProperty(value = "企业负责人")
	private String b_principal;
	@ApiModelProperty(value = "电话")
	private String b_phone;

	@ApiModelProperty(value = "采购订单明细列表")
	private List<OrderDetail> details;

}
