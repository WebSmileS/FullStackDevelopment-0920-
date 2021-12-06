package com.yrt.project.modular.warehouse.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public class WarehouseRelEmployee implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long org_id;
	
	private Long employee_inner_sn;
	
	private Long warehouse_model_inner_sn;
	
	private String employee_name;
	
	private Integer type;
}
