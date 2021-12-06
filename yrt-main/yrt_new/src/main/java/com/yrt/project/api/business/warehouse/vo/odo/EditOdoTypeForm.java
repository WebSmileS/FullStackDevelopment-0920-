package com.yrt.project.api.business.warehouse.vo.odo;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EditOdoTypeForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 当前机构ID */
	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value="出库单类型ID",required=true)
	@NotNull(message = "入库单类型ID不能为空")
	private Long type_inner_sn;
	/** 仓库类别名称 */
	@ApiModelProperty(value="仓库类别名称",required=true)
	@NotBlank(message = "仓库类别名称不能为空")
	private String name;
	
	@ApiModelProperty(value="仓库类别状态0-无效 1-有效",required=true)
	@NotNull(message = "仓库类别状态不能为空")
	private Integer status;
	
}
