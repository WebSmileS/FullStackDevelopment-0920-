package com.yrt.project.api.platform.vo.product;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PublicProductIDSForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="厂商ID",required=true)
	@NotNull(message = "厂商ID不能为空")
	private Long vendor_inner_sn;
	
	@ApiModelProperty(value="产品ID",required=true)
	@NotNull(message = "产品ID不能为空")
	private Long product_inner_sn;
	
}
