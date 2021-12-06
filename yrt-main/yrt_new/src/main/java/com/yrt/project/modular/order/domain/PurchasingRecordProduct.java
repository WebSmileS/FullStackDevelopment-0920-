package com.yrt.project.modular.order.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.BillsType;
import com.yrt.framework.aspectj.lang.annotation.Excel;
import com.yrt.framework.aspectj.lang.annotation.Excel.ColumnType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PurchasingRecordProduct {

	@ApiModelProperty(value="采购记录ID")
	private Long id;
	@ApiModelProperty(value="机构ID")
	private Long org_id;
	@ApiModelProperty(value="机构类型")
	private Integer system_type;
	@ApiModelProperty(value="业务类型")
	private BillsType bills_type;
	@ApiModelProperty(value="单据编号")
	@Excel(name = "单据编号")
	private String sn;
	private Long organization_inner_sn;
	@Excel(name = "供货单位")
	private String organization_name;
	
	private Long product_vendor_inner_sn;
	@Excel(name = "厂商")
	private String product_vendor_name;
	private Long product_inner_sn;
	@Excel(name = "产品")
	private String product_name;
	private Long specification_inner_sn;
	@Excel(name = "型号规格")
	private String specification;
	private Long unit_inner_sn;
	@Excel(name = "单位")
	private String unit;
	private Long mdrf_inner_sn;
//	@Excel(name = "医疗器械注册证号")
	private String mdrf_sn;
	@Excel(name = "生产日期", dateFormat = "yyyy-MM-dd")
	@ApiModelProperty(value = "生产日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date produce_date;
	@Excel(name = "失效日期", dateFormat = "yyyy-MM-dd")
	@ApiModelProperty(value = "失效日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date overdue_date;
	@Excel(name = "采购单价", cellType = ColumnType.NUMERIC, scale = 2)
	private BigDecimal unit_price;
	@Excel(name = "采购数量", cellType = ColumnType.NUMERIC, scale = 2)
	private BigDecimal order_quantity;
	@Excel(name = "采购税率", cellType = ColumnType.NUMERIC, scale = 2)
	private BigDecimal rate;
	@Excel(name = "采购日期", dateFormat = "yyyy-MM-dd")
	@ApiModelProperty(value = "采购日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date purchasing_date;
	@Excel(name = "医保编号")
	private String health_care_sn;
	private Long buyer_inner_sn;
	@Excel(name = "采购员")
	@ApiModelProperty(value = "采购员")
	private String buyer_name;
	private Long warehouse_inner_sn;
	@Excel(name = "仓库名称")
	private String warehouse_name;
	@Excel(name = "批号")
	private String batch_number;
	private Integer product_type;
	
	private String created_by;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date created_time;
	
	@ApiModelProperty(value="收货单明细ID,验货可用产品时需放到验货明细参数中")
	private Long arrival_detail_id;
	@ApiModelProperty(value="验货单明细ID,采购入库可用产品是需放到入库明细参数中")
	private Long inspection_detail_id;

}
