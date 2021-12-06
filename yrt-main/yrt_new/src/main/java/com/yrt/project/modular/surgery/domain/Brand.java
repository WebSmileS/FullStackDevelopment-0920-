package com.yrt.project.modular.surgery.domain;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Brand implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "品牌内部编号")
	private Long brand_inner_sn;
	@ApiModelProperty(value = "状态")
	private Integer status;
	@ApiModelProperty(value = "名称")
	private String name;

}
