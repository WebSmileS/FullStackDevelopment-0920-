package com.yrt.project.api.business.invoice.vo;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.InvoiceStatus;
import com.yrt.common.dict.PaymentMethod;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EditAdvancePaymentForm {

	@ApiModelProperty(value = "预付款ID", required = true)
	@NotNull(message = "预付款ID不能为空")
	private Long id;
	
	@ApiModelProperty(value = "相关机构ID", required = true)
	@NotNull(message = "相关机构不能为空")
	private Long organization_inner_sn;
	@ApiModelProperty(value = "相关机构名称", required = true)
	@NotBlank(message = "相关机构不能为空")
	private String organization_name;
	
	@ApiModelProperty(value = "经手人ID")
	private Long handler_inner_sn;
	@ApiModelProperty(value = "经手人")
	private String handler_name;
	@ApiModelProperty(value = "部门ID")
	private Long department_inner_sn;
	@ApiModelProperty(value = "部门名称")
	private String department_name;
	
	@ApiModelProperty(value = "单据类型", required = true)
	@NotNull(message = "单据类型不能为空")
	private BillsType bills_type;
	
	@ApiModelProperty(value = "付款日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date payment_date;
	
	@ApiModelProperty(value = "付款金额", required = true)
	@NotNull(message = "付款金额不能为空")
	@Positive(message = "付款金额必须大于0")
	private BigDecimal payment_price = BigDecimal.ZERO;
	
	@ApiModelProperty(value = "摘要")
	private String description;
	
	@ApiModelProperty(value = "付款方式")
	private PaymentMethod Payment_method;
	
	@ApiModelProperty(value = "付款状态", required = true)
	@NotNull(message = "付款状态不能为空")
	private InvoiceStatus payment_status;
	
//	@ApiModelProperty(value = "预付款附件")
//	private List<AttachmentForm> attas = new ArrayList<>();
}
