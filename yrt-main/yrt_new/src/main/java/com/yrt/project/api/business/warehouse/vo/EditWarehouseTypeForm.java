package com.yrt.project.api.business.warehouse.vo;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EditWarehouseTypeForm implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 机构编号 */
	@ApiModelProperty(value="机构ID",required=true)
	@NotNull(message = "机构ID不能为空")
	private Long org_id;
	/** 仓库类别内部编号 */
	@ApiModelProperty(value="仓库类别ID",required=true)
	@NotNull(message = "机构ID不能为空")
	private Long type_inner_sn;
	/** 仓库类别名称 */
	@ApiModelProperty(value="仓库类别名称",required=true)
	@NotBlank(message = "仓库类别名称不能为空")
	private String name;
}
