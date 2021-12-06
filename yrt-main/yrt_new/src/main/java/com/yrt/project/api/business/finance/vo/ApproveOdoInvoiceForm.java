package com.yrt.project.api.business.finance.vo;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.InvoiceType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ApproveOdoInvoiceForm {

	@ApiModelProperty(value = "出入库发票ID", required = true)
	@NotNull(message = "出入库发票ID不能为空")
	private Long id;
	
	@ApiModelProperty(value = "发票号", required = true)
	@NotBlank(message = "发票号不能为空")
	private String invoice_no;
	
	@ApiModelProperty(value = "开票日期", required = true)
	@JsonFormat(pattern = "yyyy-MM-dd")
	@NotNull(message = "开票日期不能为空")
	private Date invoice_date;
	
	@ApiModelProperty(value = "税率", required = true)
	@NotNull(message = "税率不能为空")
	private BigDecimal rate = BigDecimal.ZERO;
	
	@ApiModelProperty(value = "开票金额", required = true)
	@NotNull(message = "开票金额不能为空")
	@Positive(message = "开票金额必须大于0")
	private BigDecimal invoice_price = BigDecimal.ZERO;
	
	@ApiModelProperty(value = "摘要")
	private String description;
	
	@ApiModelProperty(value = "发票类型", required = true)
	@NotNull(message = "发票类型不能为空")
	private InvoiceType invoice_type;
}
