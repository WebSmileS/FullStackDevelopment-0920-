package com.yrt.project.modular.product.domain;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProductBasic implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "厂商内部编号 ")
	private Long vendor_inner_sn;
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
	@ApiModelProperty(value = "产品厂商自定编号(编码)")
	private String specification_vendor_sn;
	@ApiModelProperty(value = "产品图片地址")
	private String image;
	
}
