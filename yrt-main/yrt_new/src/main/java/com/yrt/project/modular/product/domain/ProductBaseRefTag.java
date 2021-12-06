package com.yrt.project.modular.product.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProductBaseRefTag {

	@ApiModelProperty(value = "产品厂商ID")
	private Long product_vendor_inner_sn;
	@ApiModelProperty(value = "产品厂商")
	private String product_vendor_name;
	@ApiModelProperty(value = "产品ID")
	private Long product_inner_sn;
	@ApiModelProperty(value = "产品")
	private String product_name;
	@ApiModelProperty(value = "型号规格ID")
	private Long specification_inner_sn;
	@ApiModelProperty(value = "型号规格")
	private String specification;
	@ApiModelProperty(value = "UDI条码")
	private String udi_barcode;
	
	private boolean status;
}
