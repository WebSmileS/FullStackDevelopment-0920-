package com.yrt.project.api.business.productSn.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.yrt.common.dict.ProductSnType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProductSnIDForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "厂商ID", required = true)
	@NotNull(message="厂商ID不能为空")
	private Long vendor_inner_sn;
	
	@ApiModelProperty(value = "产品ID", required = true)
	@NotNull(message="产品ID不能为空")
	private Long product_inner_sn;
	
	@ApiModelProperty(value = "型号规格ID", required = true)
	@NotNull(message="型号规格ID不能为空")
	private Long specification_inner_sn;
	
	@ApiModelProperty(value = "产品编号ID")
	private Long sn_inner_sn;
	
	@ApiModelProperty(value = "产品编码类型", required = true)
	@NotNull(message="产品编码类型不能为空")
	private ProductSnType type;
}
