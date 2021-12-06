package com.yrt.project.api.business.surgery.plan.vo;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.yrt.common.utils.sql.SqlUtil;
import com.yrt.project.modular.surgery.OisInstanceStatus;
import com.yrt.project.modular.surgery.OperationPlanStatus;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description="手术计划询条件: 骨科助理【待办: statuses=[DRAFT, CONFIRMED, PUBLISHED]; 待销售: packageStatuses=[BACK]; 历史: statuses=[CLOSED]】。仓库【出库: packageStatuses=[LOCKED]; 回库: packageStatuses=[OUT]】。医院【待办: statuses=[DRAFT, CONFIRMED, PUBLISHED]; 待结算: packageStatuses=[SALE_PENDING]; 历史: statuses=[CLOSED]】")
public class PlanCondition {

	@ApiModelProperty(value = "手术类型[数组]", required=false)
	private List<Byte> types;
	
	@ApiModelProperty(value = "计划状态[数组]", required=false)
	private List<OperationPlanStatus> statuses;
	
	@ApiModelProperty(value = "手术时间区间(开始)", required=false)
	private Date operationTimeFrom;
	
	@ApiModelProperty(value = "手术时间区间(结束)", required=false)
	private Date operationTimeTo;

	@ApiModelProperty(value = "查询文本(name/keywords)", required=false)
	private String q;
	
	////////////////////////////////////////////////////////
	
	@ApiModelProperty(value = "手术包状态[数组](仅经销商查询有效)", required=false)
	private List<OisInstanceStatus> packageStatuses;
	
	@ApiModelProperty(value = "医院名称(仅经销商查询有效)", required=false)
	private String hospitalName;
	
	public void normallize() {
		if(types!=null && types.isEmpty()) {
			types = null;
		}
		if(statuses!=null && statuses.isEmpty()) {
			statuses = null;
		}
		if(packageStatuses!=null && packageStatuses.isEmpty()) {
			packageStatuses = null;
		}
		if(operationTimeFrom != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(operationTimeFrom);
		}
		
		q = SqlUtil.containsLikeValue(q);
		hospitalName = SqlUtil.containsLikeValue(hospitalName);
		
	}
}
