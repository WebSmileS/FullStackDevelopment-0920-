package com.yrt.project.modular.order.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Sl implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "相关机构内部编号")
	private Long organization_inner_sn;
	@ApiModelProperty(value = "相关机构名称")
	private String organization_name;
	@ApiModelProperty(value = "机构内部编号")
	private Long org_id;
	@ApiModelProperty(value = "销售单内部编号")
	private Long sl_inner_sn;
	@ApiModelProperty(value = "销售员内部编号")
	private Long seller_inner_sn;
	@ApiModelProperty(value = "销售员名称")
	private String seller_name;
	@ApiModelProperty(value = "制单人内部编号")
	private Long making_people_inner_sn;
	@ApiModelProperty(value = "制单人名称")
	private String making_people_name;
	@ApiModelProperty(value = "部门内部编号")
	private Long department_inner_sn;
	@ApiModelProperty(value = "部门名称")
	private String department_name;
	@ApiModelProperty(value = "审核人内部编号")
	private Long auditor_inner_sn;
	@ApiModelProperty(value = "审核人名称")
	private String auditor_name;
	@ApiModelProperty(value = "状态  1-正式 2-已审核 3-已终止(已废弃) 4-已生成出库单 5-已出库 6-已配送 7-已送达 8-已收货 ")
	private Integer status;
	/** 送货日期 */
//	@JsonFormat(pattern = "yyyy-MM-dd")
//	private Date delivery_date;
	@ApiModelProperty(value = "收款日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date collection_date;
	@ApiModelProperty(value = "开单时间   以“正式”状态的单据时间为开单时间。")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date billing_time;
	@ApiModelProperty(value = "销售单编号")
	private String sl_sn;
	@ApiModelProperty(value = "采购方联系人")
	private String purchaser_linkman;
	@ApiModelProperty(value = "采购方联系电话")
	private String purchaser_phone;
	@ApiModelProperty(value = "销售单关联单据")
	private Long voucher_inner_sn_c;
	@ApiModelProperty(value = "销售方联系人")
	private String seller_linkman;
	@ApiModelProperty(value = "销售方联系电话")
	private String seller_phone;
	@ApiModelProperty(value = "描述")
	private String description;
	/** 送(收)货地址 */
//	private String delivery_address;
	@ApiModelProperty(value="单据类型",required=true)
	private BillsType billsType;
	
	@ApiModelProperty(value = "销售单明细列表")
	private List<SlDetail> details;
}
