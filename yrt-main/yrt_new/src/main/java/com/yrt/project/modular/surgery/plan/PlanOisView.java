package com.yrt.project.modular.surgery.plan;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

public class PlanOisView extends OrthopaedicOperationPlanOisInstance {

	@Getter
	@Setter
	@ApiModelProperty("plan-医院名称")
	private String hospital_name;

	@Getter
	@Setter
	@ApiModelProperty("plan-计划名称")
	private String plan_name;

	@Getter
	@Setter
	@ApiModelProperty("plan-医院科室名称")
	private String hospital_department_name;

	@Getter
	@Setter
	@ApiModelProperty("plan-医生姓名")
	private String doctor_name;

	@Getter
	@Setter
	@ApiModelProperty("plan-被手术人")
	private String patient_name;

	@Getter
	@Setter
	@ApiModelProperty("plan-手术时间")
	private Date operation_time;
	
	@Getter
	@Setter
	@ApiModelProperty("手术包结算总价(for mobile)")
	private BigDecimal total_money;

}
