package com.yrt.project.api.business.productSn.vo;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class ProductSnForm extends ProductSnIDForm {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "编号")
	private String sn;
	
	@ApiModelProperty(value = "是否默认", required = true)
	@NotNull(message="是否默认不能为空")
	private Boolean is_default = true;
	
}
