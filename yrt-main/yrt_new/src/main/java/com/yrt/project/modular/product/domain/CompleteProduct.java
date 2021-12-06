package com.yrt.project.modular.product.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CompleteProduct {

	@ApiModelProperty(value = "厂商ID ")
	private Long product_vendor_inner_sn;
	@ApiModelProperty(value = "厂商名称")
	private String product_vendor_name;
	@ApiModelProperty(value = "产品ID")
	private Long product_inner_sn;
	@ApiModelProperty(value = "产品名称")
	private String product_name;
	@ApiModelProperty(value = "型号规格ID")
	private Long specification_inner_sn;
	@ApiModelProperty(value = "型号规格")
	private String specification;
	@ApiModelProperty(value = "单位ID")
	private Long unit_inner_sn;
	@ApiModelProperty(value = "单位名称")
	private String unit;
}
