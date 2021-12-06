package com.yrt.project.modular.product.domain;

import java.io.Serializable;

import com.yrt.common.dict.ProductSnType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProductSn implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "厂商ID")
	private Long vendor_inner_sn;
	
	@ApiModelProperty(value = "产品ID")
	private Long product_inner_sn;
	
	@ApiModelProperty(value = "型号规格ID")
	private Long specification_inner_sn;
	
	@ApiModelProperty(value = "产品编码类型")
	private ProductSnType type;
	
	@ApiModelProperty(value = "编号ID")
	private Long sn_inner_sn;
	
	@ApiModelProperty(value = "编号")
	private String sn;
	
	private Boolean is_default;
}
