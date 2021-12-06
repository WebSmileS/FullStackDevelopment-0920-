package com.yrt.project.modular.gsp.domain;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WarehouseRefEmployment {

	@ApiModelProperty(value = "仓库ID", required = true)
	@NotNull(message = "仓库ID不能为空")
	private Long warehouse_inner_sn;
	@ApiModelProperty(value = "仓库名称", required = true)
	private String warehouse_name;
	
	@ApiModelProperty(value = "收货人")
	private String arrival_by;
	@ApiModelProperty(value = "验货人")
	private String inspection_by;
}
