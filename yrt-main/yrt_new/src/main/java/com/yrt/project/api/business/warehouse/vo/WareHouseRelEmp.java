package com.yrt.project.api.business.warehouse.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WareHouseRelEmp implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="仓库人员ID",required=true)
	@NotNull(message = "仓库人员ID不能为空")
	private Long employee_inner_sn;
	@ApiModelProperty(value="仓库人员类型 0-仓库主管 1-拣货员",required=true)
	@NotNull(message = "仓库人员类型不能为空")
	private Integer type;
}
