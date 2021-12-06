package com.yrt.project.modular.gsp.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GspSetup {

	@ApiModelProperty(value = "采购入库流程")
	private ProcurementProcess procurementProcess;
//	private SaleProcess saleProcess;
}
