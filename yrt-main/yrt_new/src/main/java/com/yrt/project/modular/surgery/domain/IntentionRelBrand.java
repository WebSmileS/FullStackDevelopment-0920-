package com.yrt.project.modular.surgery.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class IntentionRelBrand {
	@ApiModelProperty(value = "品牌id")
	private Long brand_inner_sn;

	@ApiModelProperty(value = "品牌名称")
	private String name;

	@ApiModelProperty(value = "品牌意向状态(暂时不用)")
	private Integer status = 1;
}
