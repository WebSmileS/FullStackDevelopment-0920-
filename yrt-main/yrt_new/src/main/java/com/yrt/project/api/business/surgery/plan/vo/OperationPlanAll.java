package com.yrt.project.api.business.surgery.plan.vo;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import com.yrt.project.modular.surgery.plan.OrthopaedicOperationPlan;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

public class OperationPlanAll extends OrthopaedicOperationPlan {
	
	@Valid
	@ApiModelProperty("计划明细(手术包)")
	@Getter @Setter
	public List<PlanOisPackageAll> details = new ArrayList<>();

}
