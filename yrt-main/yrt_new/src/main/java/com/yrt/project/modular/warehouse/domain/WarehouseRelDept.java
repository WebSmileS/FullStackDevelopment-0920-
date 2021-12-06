package com.yrt.project.modular.warehouse.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public class WarehouseRelDept implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 机构编号 */
	private Long org_id;
	/** 仓库编号 */
	private Long warehouse_model_inner_sn;
	/** 机构部门编号 */
	private Long department_inner_sn;

}
