package com.yrt.project.modular.invoice.domain;

import java.math.BigDecimal;

import com.yrt.common.dict.RefVoucherType;
import com.yrt.framework.aspectj.lang.annotation.Excel;
import com.yrt.framework.aspectj.lang.annotation.Excel.ColumnType;
import com.yrt.framework.aspectj.lang.annotation.Excel.Type;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SystemInvoiceDetail {

	private Long id;
	
	private Long system_invoice_id;
	
	@ApiModelProperty(value = "产品厂商ID")
	private Long product_vendor_inner_sn;
	@ApiModelProperty(value = "产品ID")
	private Long product_inner_sn;
	@ApiModelProperty(value = "型号规格ID")
	private Long specification_inner_sn;
	@ApiModelProperty(value = "单位内部编号")
	private Long unit_inner_sn;
	
	@Excel(name = "批号")
	@ApiModelProperty(value = "批号")
    private String batch_number;
	@Excel(name = "厂商名称")
	@ApiModelProperty(value = "厂商名称")
    private String product_vendor_name;
	@Excel(name = "产品名称")
	@ApiModelProperty(value = "产品名称")
    private String product_name;
	@Excel(name = "型号规格")
	@ApiModelProperty(value = "型号规格")
    private String specification;
	@Excel(name = "单位")
	@ApiModelProperty(value = "单位")
    private String unit;
	
	@Excel(name = "单价", cellType = ColumnType.NUMERIC, scale = 4)
	@ApiModelProperty(value = "单价")
    private BigDecimal unit_price = BigDecimal.ZERO;
	@Excel(name = "数量", cellType = ColumnType.NUMERIC, scale = 3)
	@ApiModelProperty(value = "数量")
    private BigDecimal quantity = BigDecimal.ZERO;
	@Excel(name = "税率", cellType = ColumnType.NUMERIC, scale = 2)
	@ApiModelProperty(value = "税率")
	private BigDecimal rate = BigDecimal.ZERO;
	@Excel(name = "开票数量", cellType = ColumnType.NUMERIC, scale = 34)
	@ApiModelProperty(value = "开票数量")
    private BigDecimal invoiced_quantity = BigDecimal.ZERO;
	@Excel(name = "备注", type = Type.EXPORT)
	private String remarks;
	
	private Integer product_type;
	private Integer voucher_type_c = RefVoucherType.NONE.value();
	private Long voucher_inner_sn_c;
	private Long voucher_detail_inner_sn_c;
	
}
