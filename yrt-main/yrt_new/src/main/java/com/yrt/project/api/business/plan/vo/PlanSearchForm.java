package com.yrt.project.api.business.plan.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PlanSearchForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="状态(待执行可选0草稿1正式2已审核，过往可选3已终止4已生成采购订单)",required=false)
	private String status;
	
	@ApiModelProperty(value="制单人",required=false)
	private String creator_name;
	
	@ApiModelProperty(value="部门ID",required=false)
	private Long department_inner_sn;
	
	@ApiModelProperty(value="单据编号",required=false)
	private String ppl_sn;

}
