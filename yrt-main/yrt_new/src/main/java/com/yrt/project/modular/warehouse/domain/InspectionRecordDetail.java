package com.yrt.project.modular.warehouse.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.RefVoucherType;
import com.yrt.framework.aspectj.lang.annotation.Excel;
import com.yrt.framework.aspectj.lang.annotation.Excel.ColumnType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 验货单明细
 * @author Runner
 *
 */
@Data
public class InspectionRecordDetail {
	
	@ApiModelProperty(value="验货单明细ID")
	private Long id;
	@ApiModelProperty(value="验货单ID")
	private Long inspection_record_id;
	
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
	@Excel(name = "到货数量", cellType = ColumnType.NUMERIC, scale = 2)
	private BigDecimal arrival_quantity;
	@Excel(name = "合格数量", cellType = ColumnType.NUMERIC, scale = 2)
	private BigDecimal qualified_quantity;
	@Excel(name = "不合格数量", cellType = ColumnType.NUMERIC, scale = 2)
	private BigDecimal unqualified_quantity;
	@Excel(name = "不合格原因及处理意见")
	private String unqualified_reason;
	
	private BigDecimal finish_quantity;
	
	private Long mdrf_inner_sn;
	@Excel(name = "医疗器械注册证号")
	private String mdrf_sn;
	@Excel(name = "生产日期", dateFormat = "yyyy-MM-dd")
	@ApiModelProperty(value = "生产日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date produce_date;
	@Excel(name = "失效日期", dateFormat = "yyyy-MM-dd")
	@ApiModelProperty(value = "失效日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date overdue_date;
	
	private Integer product_type;
	
	@Excel(name = "来源", readConverterExp = "13-采购记录")
	private RefVoucherType voucher_type_c;
	private Long voucher_inner_sn_c;
	@ApiModelProperty(value = "收货明细ID")
	private Long arrival_detail_id;
}
