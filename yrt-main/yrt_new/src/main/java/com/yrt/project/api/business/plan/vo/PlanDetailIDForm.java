package com.yrt.project.api.business.plan.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PlanDetailIDForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="机构ID",required=true)
	@NotNull(message = "机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value="采购计划ID",required=true)
	@NotNull(message = "采购计划ID不能为空")
	private Long ppl_inner_sn;

	@ApiModelProperty(value="采购计划明细ID",required=true)
	@NotNull(message = "采购计划明细ID不能为空")
	private Long detail_inner_sn;
	
	@ApiModelProperty(value="产品类型 0:经销商(自建)产品   1:厂商产品",required=true)
	@NotNull(message = "产品类型不能为空")
	private Integer product_type;
}
