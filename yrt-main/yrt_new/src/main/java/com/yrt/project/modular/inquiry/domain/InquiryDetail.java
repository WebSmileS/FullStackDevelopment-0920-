package com.yrt.project.modular.inquiry.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class InquiryDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "询价机构 ")
	private Long partA;
//	@ApiModelProperty(value = "询价机构 名称")
//	private String partA_name;
	@ApiModelProperty(value = "询价单内部编号")
	private Long ilps_inner_sn;
	@ApiModelProperty(value = "明细内部编号 ")
	private Long detail_inner_sn;
	@ApiModelProperty(value = "厂商内部编号")
	private Long vendor_inner_sn;
	@ApiModelProperty(value = "厂商名称")
	private String vendor_name;
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
	@ApiModelProperty(value = "折扣")
	private BigDecimal discount;
	@ApiModelProperty(value = "税率")
	private BigDecimal rate;
	@ApiModelProperty(value = "排序号")
	private Long sort_number;
	@ApiModelProperty(value="单据类型",required=true)
	private BillsType billsType;
}
