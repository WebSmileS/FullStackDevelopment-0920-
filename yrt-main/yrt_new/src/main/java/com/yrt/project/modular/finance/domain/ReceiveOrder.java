package com.yrt.project.modular.finance.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.PaymentMethod;
import com.yrt.common.dict.ReceiveStatus;
import com.yrt.framework.aspectj.lang.annotation.Excel;
import com.yrt.framework.aspectj.lang.annotation.Excel.ColumnType;
import com.yrt.framework.aspectj.lang.annotation.Excel.Type;

import lombok.Data;

@Data
public class ReceiveOrder {

	private Long id;
	
	private Long org_id;
	
	private Integer system_type;
	
	private BillsType bills_type;
	
	private Long organization_inner_sn;
	@Excel(name = "机构名称", cellType = ColumnType.STRING)
	private String organization_name;
	
	@Excel(name = "系统收款编号", type = Type.EXPORT)
	private String receive_sn;
	
	@Excel(name = "收款日期", dateFormat = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date receive_date;
	
	@Excel(name = "收款金额", cellType = ColumnType.NUMERIC, scale = 2)
	private BigDecimal receive_price = BigDecimal.ZERO;
	
	@Excel(name = "摘要", type = Type.EXPORT)
	private String description;
	
	@Excel(name = "作废原因", type = Type.EXPORT)
	private String cancel_reason;
	
	@Excel(name = "收款方式", readConverterExp = "REMITTANCE=汇款,CHECK=支票,OTHERS=其他", type = Type.EXPORT)
	private PaymentMethod receive_method;
	
	@Excel(name = "收款状态", readConverterExp = "TO_BE_COLLECTED=待收款,RECEIVED=已收款,RECORDED=已入账", type = Type.EXPORT)
	private ReceiveStatus receive_status;
	
	@Excel(name = "是否作废", readConverterExp = "0=未作废,1=作废", type = Type.EXPORT)
	private Integer is_cancel;
	
	@Excel(name = "创建人", type = Type.EXPORT)
	private String created_by;
	@Excel(name = "创建时间", dateFormat = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date created_time;
	
	@Excel(name = "收款人", type = Type.EXPORT)
	private String receive_by;
	@Excel(name = "收款时间", dateFormat = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date receive_time;
	
	@Excel(name = "入账人", type = Type.EXPORT)
	private String entry_by;
	@Excel(name = "入账时间", dateFormat = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date entry_time;
	
	@Excel(name = "作废人", type = Type.EXPORT)
	private String cancel_by;
	@Excel(name = "作废时间", dateFormat = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date cancel_time;
	
	private List<ReceiveOrderDetail> details;
}
