package com.yrt.project.modular.warehouse.domain;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CheckPurchasingRecord {
	
	private boolean isExist = false;
	private BigDecimal order_quantity;
	private BigDecimal arrival_quantity;
	
	private BigDecimal qualified_quantity;
	private BigDecimal unqualified_quantity;
	
	private String batch_number;
	
	private Long voucher_inner_sn_c;
	
	private Long warehouse_inner_sn;
	private String warehouse_name;
	
	private Long mdrf_inner_sn;
	private String mdrf_sn;
	private Date produce_date;
	private Date overdue_date;
	
	private Long arrival_detail_id;
	private Long inspection_detail_id;

}
