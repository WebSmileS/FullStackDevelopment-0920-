package com.yrt.project.api.business.tag.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddUDIRefproductForm {

	@ApiModelProperty(value = "产品厂商ID", required = true)
	@NotNull(message = "产品厂商ID不能为空")
	private Long vendor_inner_sn;
	@ApiModelProperty(value = "产品ID", required = true)
	@NotNull(message = "产品ID不能为空")
	private Long product_inner_sn;
	@ApiModelProperty(value = "型号规格ID", required = true)
	@NotNull(message = "型号规格ID不能为空")
	private Long specification_inner_sn;
	@ApiModelProperty(value = "标签/UDI条码", required = true)
	@NotBlank(message = "标签/UDI条码不能为空")
	private String udi_barcode;
	
}
