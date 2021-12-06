package com.yrt.project.modular.warehouse.domain;

import java.math.BigDecimal;

import com.yrt.common.dict.RefVoucherType;
import com.yrt.framework.aspectj.lang.annotation.Excel;
import com.yrt.framework.aspectj.lang.annotation.Excel.ColumnType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ArrivalRecordDetail {
	
	@ApiModelProperty(value="收货单明细ID")
	private Long id;
	@ApiModelProperty(value="收货单ID")
	private Long arrival_record_id;
	
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
	@Excel(name = "批号")
	private String batch_number;
	@Excel(name = "采购数量", cellType = ColumnType.NUMERIC, scale = 2)
	private BigDecimal order_quantity;
	@Excel(name = "到货数量", cellType = ColumnType.NUMERIC, scale = 2)
	private BigDecimal arrival_quantity;
	private BigDecimal finish_quantity;
	private Integer product_type;
	
	@Excel(name = "来源", readConverterExp = "13-采购记录")
	private RefVoucherType voucher_type_c;
	private Long voucher_inner_sn_c;
}
