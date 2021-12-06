package com.yrt.project.modular.warehouse.domain;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WarehouseType implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "机构编号")
	private Long org_id;
	@ApiModelProperty(value = "仓库类别内部编号")
	private Long type_inner_sn;
	@ApiModelProperty(value = "仓库类别名称")
	private String name;
	@ApiModelProperty(value = "状态")
	private Integer status;
}
