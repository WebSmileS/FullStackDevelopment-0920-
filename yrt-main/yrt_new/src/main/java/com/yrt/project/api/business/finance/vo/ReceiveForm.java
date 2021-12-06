package com.yrt.project.api.business.finance.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.PaymentMethod;
import com.yrt.common.dict.ReceiveStatus;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ReceiveForm {

	@ApiModelProperty(value = "编辑时候需要的收款单ID")
	private Long id;
	
	@ApiModelProperty(value = "单据类型", required = true)
	@NotNull(message = "单据类型不能为空")
	private BillsType bills_type;
	
	@ApiModelProperty(value = "相关机构ID", required = true)
	@NotNull(message = "相关机构不能为空")
	private Long organization_inner_sn;
	@ApiModelProperty(value = "相关机构名称", required = true)
	@NotBlank(message = "相关机构不能为空")
	private String organization_name;
	
	@ApiModelProperty(value = "收款日期", required = true)
	@JsonFormat(pattern = "yyyy-MM-dd")
	@NotNull(message = "收款日期不能为空")
	private Date receive_date;
	
	@ApiModelProperty(value = "收款金额", required = true)
	@NotNull(message = "收款金额不能为空")
	@Positive(message = "收款金额必须大于0")
	private BigDecimal receive_price = BigDecimal.ZERO;
	
	@ApiModelProperty(value = "摘要")
	private String description;
	
	@ApiModelProperty(value = "收款方式", required = true)
	@NotNull(message = "收款方式不能为空")
	private PaymentMethod receive_method;
	
	@ApiModelProperty(value = "收款状态", required = true)
	@NotNull(message = "收款状态不能为空")
	private ReceiveStatus receive_status;
	
	@ApiModelProperty(value = "收款明细", required = true)
	@NotEmpty(message = "收款明细不能为空")
	private List<PaymentDetailForm> details;
}
