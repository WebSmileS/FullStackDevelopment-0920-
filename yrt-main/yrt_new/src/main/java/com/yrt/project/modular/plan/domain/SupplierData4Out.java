package com.yrt.project.modular.plan.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class SupplierData4Out implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private BigDecimal unit_price;
	
	private Long supplier_id;
	
	private String supplier_name;
	
	private Long unit_inner_sn;
	
	private String unit_name;
	
	private Integer supplier_type;
	
	private String customID;
	
	private BigDecimal measure;
}
