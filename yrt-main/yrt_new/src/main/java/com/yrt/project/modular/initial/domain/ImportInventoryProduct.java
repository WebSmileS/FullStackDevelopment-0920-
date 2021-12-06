package com.yrt.project.modular.initial.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.utils.Utils;
import com.yrt.framework.aspectj.lang.annotation.Excel;
import com.yrt.framework.aspectj.lang.annotation.Excel.ColumnType;
import com.yrt.framework.aspectj.lang.annotation.Excel.Type;

import lombok.Data;

@Data
public class ImportInventoryProduct {

	private Long org_id;
	
	@Excel(name = "序号")
	private Long sn;
	
//	@Excel(name = "ID", cellType = ColumnType.STRING, type = Type.EXPORT)
	private Long id;
	
//	@Excel(name = "仓库名称ID", type = Type.EXPORT)
	private Long warehouse_id;
	
	@Excel(name = "仓库名称", cellType = ColumnType.STRING)
	private String warehouse_name;
	
//	@Excel(name = "厂商名称ID", type = Type.EXPORT)
	private Long vendor_id;
	
	@Excel(name = "厂商名称", cellType = ColumnType.STRING)
	private String vendor_name;
	
//	@Excel(name = "产品名称ID", type = Type.EXPORT)
	private Long product_id;
	
	@Excel(name = "产品名称", cellType = ColumnType.STRING)
	private String product_name;
	
//	@Excel(name = "产品规格型号ID", type = Type.EXPORT)
	private Long product_spec_id;
	
	@Excel(name = "产品规格型号", cellType = ColumnType.STRING)
	private String product_spec;
	
//	@Excel(name = "产品包装单位ID", type = Type.EXPORT)
	private Long product_unit_id;
	
	@Excel(name = "产品包装单位", cellType = ColumnType.STRING)
	private String product_unit;
	
	@Excel(name = "批号", cellType = ColumnType.STRING)
	private String batch_number;
	
	@Excel(name = "生产日期", dateFormat = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date produce_date;
	
	@Excel(name = "失效日期", dateFormat = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date overdue_date;
	
	@Excel(name = "数量", cellType = ColumnType.NUMERIC, scale = 2)
	private BigDecimal quantity = BigDecimal.ZERO;
	
	@Excel(name = "单价", cellType = ColumnType.NUMERIC, scale = 2)
	private BigDecimal unit_price = BigDecimal.ZERO;
	
//	@Excel(name = "创建时间", dateFormat = "yyyy-MM-dd", type = Type.EXPORT)
	private Date create_date;
//	@Excel(name = "创建人ID", type = Type.EXPORT)
	private Long create_id;
//	@Excel(name = "创建人", type = Type.EXPORT)
	private String create_by;
	
//	@Excel(name = "产品类型", readConverterExp = "0=自建,1=平台", type = Type.EXPORT)
	private int product_type = 1;
	
	//状态 0-没有建账 1-建账成功 2-建账失败
	@Excel(name = "状态", readConverterExp = "0=没有建账,1=成功,2=失败", type = Type.EXPORT)
	private Integer status = 0;
	
	@Excel(name = "错误信息", cellType = ColumnType.STRING, type = Type.EXPORT)
	private String error_msg;
	
	public void trim() {
		warehouse_name = Utils.trimObject(warehouse_name);
		vendor_name = Utils.trimObject(vendor_name);
		product_name = Utils.trimObject(product_name);
		product_spec = Utils.trimObject(product_spec);
		product_unit = Utils.trimObject(product_unit);
		batch_number = Utils.trimObject(batch_number);
	}
}
