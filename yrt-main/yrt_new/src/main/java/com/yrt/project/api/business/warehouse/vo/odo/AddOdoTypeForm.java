package com.yrt.project.api.business.warehouse.vo.odo;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddOdoTypeForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 机构编号 */
	/** 当前机构ID */
	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long org_id;
	/** 仓库类别名称 */
	@ApiModelProperty(value="仓库类别名称",required=true)
	@NotBlank(message = "类别输入不能为空")
	private String name;
	/** 类别状态 */
	@ApiModelProperty(value="类别状态",required=false)
	private Long status;
	
	
}
