package com.yrt.project.api.business.plan.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.yrt.common.utils.StringUtils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EditPlanForm implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 机构内部编号 */
	@ApiModelProperty(value="机构ID",required=true)
	@NotNull(message = "机构ID不能为空")
	private Long org_id;
	/** 采购计划单内部编号 */
	@ApiModelProperty(value="采购计划单ID",required=true)
	@NotNull(message = "采购计划单ID不能为空")
	private Long ppl_inner_sn;
	/** 部门内部编号 */
	@ApiModelProperty(value="部门ID",required=false)
	private Long department_inner_sn;
	private Long department_inner_sn_old;
	@ApiModelProperty(value="部门",required=false)
	private String department_name;
	private String department_name_old;
	/** 状态 */
	@ApiModelProperty(value="状态",required=true)
	@NotBlank(message = "状态不能为空")
	private String status;
	/** 采购计划单编号 */
	@ApiModelProperty(value="采购计划单编号",required=false)
	private String ppl_sn;
	private String ppl_sn_old;
	/** 描述 */
	@ApiModelProperty(value="描述",required=false)
	private String description;
	private String description_old;
	
	@ApiModelProperty(value="采购计划明细列表",required=false)
	private List<PlanDetailForm> detailList = new ArrayList<>();
	
	@ApiModelProperty(hidden = true)
	private Map<String,Object> info = new HashMap<>();
	
	@ApiModelProperty(hidden = true)
	public Map<String,Object> getInfo() {
		if(StringUtils.isNotNull(department_inner_sn) || StringUtils.isNotNull(department_inner_sn_old)) {
			if(StringUtils.isNull(department_inner_sn)) {
				department_inner_sn = -1L;
				department_name = "";
			}
			info.put("department_inner_sn", department_inner_sn);
			info.put("department_name", department_name);
		}
		if((StringUtils.isNotEmpty(ppl_sn) || StringUtils.isNotEmpty(ppl_sn_old))) {
			info.put("ppl_sn", ppl_sn);
		}
		if((StringUtils.isNotEmpty(description) || StringUtils.isNotEmpty(description_old))) {
			info.put("description", description);
		}
		return info;
	}
}
