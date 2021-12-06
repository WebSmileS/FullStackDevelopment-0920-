package com.yrt.project.modular.invoice.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.InvoiceStatus;
import com.yrt.common.dict.PaymentMethod;
import com.yrt.framework.aspectj.lang.annotation.Excel;
import com.yrt.framework.aspectj.lang.annotation.Excel.ColumnType;
import com.yrt.framework.aspectj.lang.annotation.Excel.Type;

import lombok.Data;

@Data
public class AdvancePayment {

	private Long id;
	
	private Long org_id;
	
	private Integer system_type;
	
	private Long organization_inner_sn;
	@Excel(name = "机构名称", cellType = ColumnType.STRING)
	private String organization_name;
	
	private Long handler_inner_sn;
	@Excel(name = "经手人", cellType = ColumnType.STRING)
	private String handler_name;
	
	private Long department_inner_sn;
	@Excel(name = "部门", cellType = ColumnType.STRING)
	private String department_name;
	
	private BillsType bills_type;
	
	@Excel(name = "系统付款编号", type = Type.EXPORT)
	private String payment_sn;
	
	@Excel(name = "付款日期", dateFormat = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date payment_date;
	
	@Excel(name = "付款金额", cellType = ColumnType.NUMERIC, scale = 2)
	private BigDecimal payment_price = BigDecimal.ZERO;
	
	@Excel(name = "核销金额", cellType = ColumnType.NUMERIC, scale = 2)
	private BigDecimal written_off_price = BigDecimal.ZERO;
	
	@Excel(name = "摘要", type = Type.EXPORT)
	private String description;
	
	@Excel(name = "作废原因", type = Type.EXPORT)
	private String cancel_reason;
	
	@Excel(name = "付款方式", readConverterExp = "REMITTANCE=汇款,CHECK=支票,OTHERS=其他", type = Type.EXPORT)
	private PaymentMethod Payment_method;
	
	@Excel(name = "付款状态", readConverterExp = "DRAFT=草稿,PENDING=待审核,NOT_WRITTEN_OFF=未核销,PARTIAL_WRITE_OFF=部分核销,WRITTEN_OFF=已核销", type = Type.EXPORT)
	private InvoiceStatus payment_status;
	
	@Excel(name = "是否作废", readConverterExp = "0=未作废,1=作废", type = Type.EXPORT)
	private Integer is_cancel;
	
	@Excel(name = "创建人", type = Type.EXPORT)
	private String created_by;
	@Excel(name = "创建时间", dateFormat = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date created_time;
	
	@Excel(name = "审核人", type = Type.EXPORT)
	private String confirm_by;
	@Excel(name = "审核时间", dateFormat = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date confirm_time;
	
	@Excel(name = "作废人", type = Type.EXPORT)
	private String cancel_by;
	@Excel(name = "作废时间", dateFormat = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date cancel_time;
	
	private List<PaymentAttachment> attas;
}
