package com.yrt.project.modular.product.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data(staticConstructor = "of")
public class UDIRefProduct {

	@ApiModelProperty(value = "产品厂商ID")
	private final Long vendor_inner_sn;
	@ApiModelProperty(value = "产品ID")
	private final Long product_inner_sn;
	@ApiModelProperty(value = "型号规格ID")
	private final Long specification_inner_sn;
	@ApiModelProperty(value = "UDI条码")
	private final String udi_barcode;
}
