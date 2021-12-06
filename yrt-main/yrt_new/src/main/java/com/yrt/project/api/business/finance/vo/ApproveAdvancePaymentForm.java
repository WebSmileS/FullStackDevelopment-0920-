package com.yrt.project.api.business.finance.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.PaymentMethod;
import com.yrt.project.api.business.invoice.vo.AttachmentForm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ApproveAdvancePaymentForm {

	@ApiModelProperty(value = "预付款ID", required = true)
	@NotNull(message = "预付款ID不能为空")
	private Long id;
	
	@ApiModelProperty(value = "付款日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date payment_date;
	
	@ApiModelProperty(value = "付款金额", required = true)
	@NotNull(message = "付款金额不能为空")
	@Positive(message = "付款金额必须大于0")
	private BigDecimal payment_price = BigDecimal.ZERO;
	
	@ApiModelProperty(value = "摘要")
	private String description;
	
	@ApiModelProperty(value = "付款方式", required = true)
	@NotNull(message = "付款方式不能为空")
	private PaymentMethod Payment_method;
	
	@ApiModelProperty(value = "预付款附件")
	private List<AttachmentForm> attas = new ArrayList<>();
}
