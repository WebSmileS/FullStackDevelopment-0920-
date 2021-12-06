package com.yrt.project.api.business.finance.vo;

import java.math.BigDecimal;

import com.yrt.framework.aspectj.lang.annotation.Excel;
import com.yrt.framework.aspectj.lang.annotation.Excel.Type;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PaymentDetailForm {

	@ApiModelProperty(value = "预付款编号或系统发票编号")
	private String sn;
	
	@ApiModelProperty(value = "发票号")
	@Excel(name = "发票号", type = Type.EXPORT)
	private String invoice_no;
	
	@ApiModelProperty(value = "开票金额")
	private BigDecimal invoice_price = BigDecimal.ZERO;
	
	@ApiModelProperty(value = "核销金额")
	private BigDecimal written_off_price = BigDecimal.ZERO;
	
	@ApiModelProperty(value = "调单类型 10-入库发票 11-出库发票 12-预付款")
	private Integer voucher_type_c;
	@ApiModelProperty(value = "入库发票、出库发票或预付款ID")
	private Long voucher_inner_sn_c;
}
