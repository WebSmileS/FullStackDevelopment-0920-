package com.yrt.project.api.business.finance.vo;

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchPaymentInvoiceForm {

	@ApiModelProperty(value = "相关机构ID")
	private Long organization_inner_sn;
	@ApiModelProperty(value = "发票号/付款编号")
	private String invoice_no;
	
	@ApiModelProperty(value="单据类型",required=true)
	@NotNull(message = "单据类型不能为空")
	private BillsType billsType;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date begin_date;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date;
}
