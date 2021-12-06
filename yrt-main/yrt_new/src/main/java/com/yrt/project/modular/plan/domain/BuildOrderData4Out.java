package com.yrt.project.modular.plan.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class BuildOrderData4Out implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 厂商内部编号如果是经销商自建产品则是经销商ID */
	private Long product_vendor_inner_sn;
	private String product_vendor_name;
	/** 产品内部编号 */
	private Long product_inner_sn;
	private String product_name;
	/** 型号规格内部编号 */
	private Long specification_inner_sn;
	private String specification;
	/** 单位内部编号 */
	private Long unit_inner_sn;
	private String unit_name;
	/** 数量 */
	private BigDecimal quantity;
	
	private List<SupplierData4Out> supplierList = new ArrayList<>();
	
	private Integer product_type;
	private String batch_number;
}
