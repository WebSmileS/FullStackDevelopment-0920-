package com.yrt.project.api.business.surgery.plan.vo;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import com.yrt.project.modular.surgery.plan.OrthopaedicOperationPlanOisInstance;
import com.yrt.project.modular.surgery.plan.OrthopaedicOperationPlanOisProductDetail;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


public class PlanOisPackageAll extends OrthopaedicOperationPlanOisInstance {
	
	@Valid
	@ApiModelProperty("工具包")
	@Getter @Setter
	private List<PlanKitAll> kits = new ArrayList<>();
	
	@Valid
	@ApiModelProperty("手术包明细（耗材）")
	@Getter @Setter
	private List<OrthopaedicOperationPlanOisProductDetail> details = new ArrayList<>();
	
}
