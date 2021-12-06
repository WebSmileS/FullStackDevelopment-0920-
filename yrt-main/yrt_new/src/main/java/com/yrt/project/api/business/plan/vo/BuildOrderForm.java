package com.yrt.project.api.business.plan.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BuildOrderForm implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 当前机构ID */
	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value="生成采购订单数据",required=true)
	private List<BuildOrderDetailForm> buildData = new ArrayList<>();
	
	@ApiModelProperty(value="原采购计划明细ID列表",required=true)
	private List<PlanDetailIDForm> detail = new ArrayList<>();
}
