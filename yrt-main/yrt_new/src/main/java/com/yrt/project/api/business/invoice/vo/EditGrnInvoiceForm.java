package com.yrt.project.api.business.invoice.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.InvoiceStatus;
import com.yrt.common.dict.InvoiceType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EditGrnInvoiceForm {

	@ApiModelProperty(value = "出入库发票ID", required = true)
	@NotNull(message = "出入库发票ID不能为空")
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
	
	@ApiModelProperty(value = "发票状态", required = true)
	@NotNull(message = "发票状态不能为空")
	private InvoiceStatus invoice_status;
	
	@ApiModelProperty(value = "发票明细", required = true)
	@NotEmpty(message = "发票明细不能为空")
	private List<InvoiceDetailForm> detail;
}
