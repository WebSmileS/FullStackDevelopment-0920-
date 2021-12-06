package com.yrt.project.modular.finance.domain;

import java.math.BigDecimal;

import com.yrt.framework.aspectj.lang.annotation.Excel;
import com.yrt.framework.aspectj.lang.annotation.Excel.ColumnType;
import com.yrt.framework.aspectj.lang.annotation.Excel.Type;

import lombok.Data;

@Data
public class PaymentOrderDetail {

	private Long id;
	
	private Long payment_order_id;
	
	@Excel(name = "预付款编号或系统发票编号", type = Type.EXPORT)
	private String sn;
	
	@Excel(name = "发票号", type = Type.EXPORT)
	private String invoice_no;
	
	@Excel(name = "开票金额", cellType = ColumnType.NUMERIC, scale = 2)
	private BigDecimal invoice_price = BigDecimal.ZERO;
	
	@Excel(name = "核销金额", cellType = ColumnType.NUMERIC, scale = 2)
	private BigDecimal written_off_price = BigDecimal.ZERO;
	
	private Integer voucher_type_c;
	private Long voucher_inner_sn_c;
}
