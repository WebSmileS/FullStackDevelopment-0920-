package com.yrt.project.api.business.invoice.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.InvoiceStatus;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchInvoiceForm {

	@ApiModelProperty(value = "相关机构ID")
	private Long organization_inner_sn;
	@ApiModelProperty(value = "发票号/付款编号")
	private String invoice_no;
	@ApiModelProperty(value = "发票状态 默认ALL-全部， DRAFT-草稿 PENDING-待审核 NOT_WRITTEN_OFF-未核销 PARTIAL_WRITE_OFF-部分核销 WRITTEN_OFF-已核销")
	private InvoiceStatus invoice_status = InvoiceStatus.ALL;
	@ApiModelProperty(value = "是否作废 默认0，0-未作废 1-作废")
	private int isCancel = 0;
	
	@ApiModelProperty(value="单据类型")
	private BillsType billsType;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date begin_date;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date;
}
