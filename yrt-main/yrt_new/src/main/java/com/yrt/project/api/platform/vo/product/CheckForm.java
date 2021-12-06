package com.yrt.project.api.platform.vo.product;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CheckForm implements Serializable {

	private static final long serialVersionUID = 1L;

	public interface CheckProduct{};
	public interface CheckProductSpec{};
	public interface CheckProductMDRF{};
	
	@ApiModelProperty(value="厂商ID")
	@NotNull(groups = {CheckProduct.class, CheckProductSpec.class}, message = "厂商ID不能为空")
	private Long vendor_inner_sn;
	
	@ApiModelProperty(value="产品ID")
	@NotNull(groups = {CheckProductSpec.class}, message = "产品ID不能为空")
	private Long product_inner_sn;
	
	@ApiModelProperty(value="检测名称", required=true)
	@NotBlank(message = "检测名称不能为空")
	private String check_name;
	
	@ApiModelProperty(value="产品编码", required=false)
	private String specification_vendor_sn;
}
