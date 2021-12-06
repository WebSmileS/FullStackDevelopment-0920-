package com.yrt.project.modular.applyvoucher.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class HospitalApplyVoucherDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Long id;

	@ApiModelProperty(value = "医院内部编号")
	private Long hospital_inner_sn;
	@ApiModelProperty(value = "医院名称")
	private String hospital_name;
	@ApiModelProperty(value = "单据内部编号")
	private Long voucher_inner_sn;	
	@ApiModelProperty(value = "明细内部编号")
	private Long detail_inner_sn;
	@ApiModelProperty(value = "经销商内部编号")
	private Long dealer_inner_sn;
	@ApiModelProperty(value = "经销商名称")
	private String dealer_name;
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
	@ApiModelProperty(value = "排序号")
	private Integer sort_number;
	@ApiModelProperty(value = "批号")
	private String batch_number;
	@ApiModelProperty(value = "计划数量")
	private BigDecimal plan_quantity;
	/** 达成数量 */
//	private BigDecimal achieve_quantity;
	@ApiModelProperty(value = "状态 0-申领  3-申领终止(人为终止)")
	private Integer status;
	@ApiModelProperty(value = "产品类型 0:经销商(自建)产品   1:厂商产品")
	private Integer product_type;
	@ApiModelProperty(value = "图片")
	private String image;
	@ApiModelProperty(value="单价")
	private BigDecimal unit_price;
}
