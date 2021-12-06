package com.yrt.project.api.business.tag.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchProductBaseForm {

	@ApiModelProperty(value = "厂商")
	private String product_vendor_name;
	@ApiModelProperty(value = "产品")
	private String product_name;
	@ApiModelProperty(value = "型号规格")
	private String specification;
	@ApiModelProperty(value = "标签/UDI条码")
	private String udi_barcode;
}
