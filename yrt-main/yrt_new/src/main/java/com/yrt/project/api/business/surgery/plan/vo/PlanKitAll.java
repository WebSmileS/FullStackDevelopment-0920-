package com.yrt.project.api.business.surgery.plan.vo;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import com.yrt.project.modular.surgery.plan.OrthopaedicOperationPlanKitInstance;
import com.yrt.project.modular.surgery.plan.OrthopaedicOperationPlanKitProductDetail;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

public class PlanKitAll extends OrthopaedicOperationPlanKitInstance {
	
	@Valid
	@ApiModelProperty("工具包明细(工具)")
	@Getter @Setter
	private List<OrthopaedicOperationPlanKitProductDetail> details = new ArrayList<>();
	
}
