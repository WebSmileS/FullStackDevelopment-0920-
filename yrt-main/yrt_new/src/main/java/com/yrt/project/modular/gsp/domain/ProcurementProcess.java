package com.yrt.project.modular.gsp.domain;

import java.util.ArrayList;
import java.util.List;

import com.yrt.common.utils.StringUtils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProcurementProcess {

	@ApiModelProperty(value = "严格GSP流程", required = true)
	private boolean strictControl = true;
	
	@ApiModelProperty(value = "收货人")
	private String arrival_by;
	@ApiModelProperty(value = "验货人")
	private String inspection_by;
	
	private List<WarehouseRefEmployment> emplist = new ArrayList<>();
	
	public String[] getEmployment(Long warehouseId) {
		String[] res = {arrival_by, inspection_by};
		if(StringUtils.isNotEmpty(emplist)) {
			for (WarehouseRefEmployment emp : emplist) {
				if(warehouseId.equals(emp.getWarehouse_inner_sn())) {
					res[0] = emp.getArrival_by();
					res[1] = emp.getInspection_by();
				}
			}
		}
		return res;
	}
}
