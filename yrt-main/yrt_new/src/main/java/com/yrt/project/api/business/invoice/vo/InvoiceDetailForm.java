package com.yrt.project.api.business.invoice.vo;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class InvoiceDetailForm {

//	@ApiModelProperty(value = "出入库发票明细ID,编辑时必传")
//	private Long id;
	
	@ApiModelProperty(value = "出入库发票ID,编辑时必传")
	private Long system_invoice_id;
	
	@ApiModelProperty(value = "产品厂商ID")
	private Long product_vendor_inner_sn;
	@ApiModelProperty(value = "产品ID")
	private Long product_inner_sn;
	@ApiModelProperty(value = "型号规格ID")
	private Long specification_inner_sn;
	@ApiModelProperty(value = "单位内部编号")
	private Long unit_inner_sn;
	
	@ApiModelProperty(value = "批号")
    private String batch_number;
	@ApiModelProperty(value = "厂商名称")
    private String product_vendor_name;
	@ApiModelProperty(value = "产品名称")
    private String product_name;
	@ApiModelProperty(value = "型号规格")
    private String specification;
	@ApiModelProperty(value = "单位")
    private String unit;
	
	@ApiModelProperty(value = "单价")
    private BigDecimal unit_price = BigDecimal.ZERO;
	@ApiModelProperty(value = "数量")
    private BigDecimal quantity = BigDecimal.ZERO;
	@ApiModelProperty(value = "税率")
	private BigDecimal rate = BigDecimal.ZERO;
	@ApiModelProperty(value = "开票数量")
    private BigDecimal invoiced_quantity = BigDecimal.ZERO;
	@ApiModelProperty(value = "备注")
	
	private String remarks;
	
	@ApiModelProperty(value = "产品类型")
	private Integer product_type;
	@ApiModelProperty(value = "调单类型 7-出库单 9-入库单")
	private Integer voucher_type_c = 0;
	@ApiModelProperty(value = "出入库单ID")
	private Long voucher_inner_sn_c;
	@ApiModelProperty(value = "出入库单明细ID")
	private Long voucher_detail_inner_sn_c;
}
