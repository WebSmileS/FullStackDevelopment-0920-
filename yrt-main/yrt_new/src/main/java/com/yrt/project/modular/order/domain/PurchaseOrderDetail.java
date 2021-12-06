package com.yrt.project.modular.order.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.BillsType;
import com.yrt.framework.aspectj.lang.annotation.Excel;
import com.yrt.framework.aspectj.lang.annotation.Excel.ColumnType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PurchaseOrderDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "采购方机构ID")
	private Long partA;
	@ApiModelProperty(value = "销售方机构ID")
	private Long partB;
	@ApiModelProperty(value = "采购订单ID")
	private Long po_so_inner_sn;
	@ApiModelProperty(value = "采购明细ID")
	private Long detail_inner_sn;
	@ApiModelProperty(value = "产品厂商ID")
	private Long product_vendor_inner_sn;
	@ApiModelProperty(value = "产品ID")
	private Long product_inner_sn;
	@ApiModelProperty(value = "型号规格ID")
	private Long specification_inner_sn;
	@ApiModelProperty(value = "单位内部编号")
	private Long unit_inner_sn;
	
	@Excel(name = "销售方机构名称")
	@ApiModelProperty(value = "销售方机构名称")
	private String partB_name;
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
	@Excel(name = "数量", cellType = ColumnType.NUMERIC, scale = 2)
	@ApiModelProperty(value = "数量")
	private BigDecimal quantity;
	@Excel(name = "含税单价", cellType = ColumnType.NUMERIC, scale = 2)
	@ApiModelProperty(value = "含税单价")
	private BigDecimal unit_price;
	@Excel(name = "入库数量", cellType = ColumnType.NUMERIC, scale = 2)
	@ApiModelProperty(value = "入库数量")
	private BigDecimal finish_quantity = BigDecimal.ZERO;
	@Excel(name = "状态", readConverterExp = "0=未审核,1=执行中,2=完成,3=终止")
	@ApiModelProperty(value = "状态 0-未审核 1-执行中 2-完成 3-终止")
	private Integer status = 0;
	@Excel(name = "订单编号")
	@ApiModelProperty(value = "订单编号")
	private String po_sn;
	@Excel(name = "采购员")
	@ApiModelProperty(value = "采购员")
	private String buyer_name;
	@Excel(name = "部门")
	@ApiModelProperty(value = "部门")
	private String department_name;
	@Excel(name = "仓库名称")
	@ApiModelProperty(value = "仓库名称")
	private String warehouse_name;
	@Excel(name = "采购日期", dateFormat = "yyyy-MM-dd")
	@ApiModelProperty(value = "采购日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date create_time;
	
	@ApiModelProperty(value="单据类型")
	private BillsType billsType;
	@ApiModelProperty(hidden = true)
	private Integer p_status;
	@ApiModelProperty(hidden = true)
	private Integer d_status;
}
