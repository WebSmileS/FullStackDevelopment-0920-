package com.yrt.project.modular.inquiry.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class InquirySortPrice implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "报价机构")
	private Long organization_inner_sn;
	@ApiModelProperty(value = "报价机构名称")
	private String organization_name;
//	@ApiModelProperty(value = "询价单内部编号")
//	private Long ilps_inner_sn;
	@ApiModelProperty(value = "询价时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date ask_time;
	@ApiModelProperty(value = "询价单编号")
	private String ilps_sn;
	@ApiModelProperty(value = "联系人")
	private String contacts;
	@ApiModelProperty(value = "电话 ")
	private String phone;
	@ApiModelProperty(value="单据类型",required=true)
	private BillsType billsType;
	
	@ApiModelProperty(value = "厂商内部编号")
	private Long product_vendor_inner_sn;
	@ApiModelProperty(value = "厂商名称")
	private String product_vendor_name;
	@ApiModelProperty(value = "产品内部编号")
	private Long product_inner_sn;
	@ApiModelProperty(value = "产品名称")
	private String product_name;
	@ApiModelProperty(value = "型号规格内部编号")
	private Long specification_inner_sn;
	@ApiModelProperty(value = "型号规格")
	private String specification;
	@ApiModelProperty(value = "单位内部编号")
	private Long unit_inner_sn;
	@ApiModelProperty(value = "单位名称")
	private String unit_name;
	@ApiModelProperty(value = "数量")
	private BigDecimal quantity;
	@ApiModelProperty(value = "单价")
	private BigDecimal unit_price;
	@ApiModelProperty(value = "折扣单价")
	private BigDecimal discounted_price;
	@ApiModelProperty(value = "折扣")
	private BigDecimal discount;
	@ApiModelProperty(value = "税率")
	private BigDecimal rate;
	
}
