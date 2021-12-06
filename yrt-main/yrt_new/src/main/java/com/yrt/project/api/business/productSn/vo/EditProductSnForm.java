package com.yrt.project.api.business.productSn.vo;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.yrt.common.dict.ProductSnType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EditProductSnForm implements Serializable {

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
	
	@ApiModelProperty(value = "产品编号ID", required = true)
	@NotNull(message="产品编号ID不能为空")
	private Long sn_inner_sn;
	
	@ApiModelProperty(value = "编号", required = true)
	@NotBlank(message="编号不能为空")
	private String sn;
	
	@ApiModelProperty(value = "是否默认", required = true)
	@NotNull(message="是否默认不能为空")
	private Boolean is_default;
	
	@ApiModelProperty(value = "产品编码类型", required = true)
	@NotNull(message="产品编码类型不能为空")
	private ProductSnType type;
}
