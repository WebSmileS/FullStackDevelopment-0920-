package com.yrt.project.api.business.warehouse.vo;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WarehouseCheckForm implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="父仓库ID",required=false)
	private Long parent_warehouse_model_inner_sn;
	
	@ApiModelProperty(value="仓库ID",required=false)
	private Long warehouse_model_inner_sn;
	
	@ApiModelProperty(value="仓库模型编号",required=true)
	@NotBlank(message = "仓库编号不能为空")
	private String warehouse_model_sn;
}
