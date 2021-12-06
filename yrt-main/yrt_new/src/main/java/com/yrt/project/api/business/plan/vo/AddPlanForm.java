package com.yrt.project.api.business.plan.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddPlanForm implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 机构内部编号 */
	@ApiModelProperty(value="机构ID",required=true)
	@NotNull(message = "机构ID不能为空")
	private Long org_id;
	/** 部门内部编号 */
	@ApiModelProperty(value="部门ID",required=false)
	private Long department_inner_sn;
	/** 部门 */
	@ApiModelProperty(value="部门",required=false)
	private String department_name = "";
	/** 状态 */
	@ApiModelProperty(value="状态",required=true)
	@NotBlank(message = "状态不能为空")
	private String status;
	/** 采购计划单编号 */
	@ApiModelProperty(value="采购计划单编号",required=false)
	private String ppl_sn;
	/** 描述 */
	@ApiModelProperty(value="描述",required=false)
	private String description = "";
	
	@ApiModelProperty(value="采购计划明细列表",required=false)
	private List<PlanDetailForm> detailList = new ArrayList<>();
}
