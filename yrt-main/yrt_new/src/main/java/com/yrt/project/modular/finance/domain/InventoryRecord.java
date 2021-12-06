package com.yrt.project.modular.finance.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.framework.aspectj.lang.annotation.Excel;
import com.yrt.framework.aspectj.lang.annotation.Excel.ColumnType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class InventoryRecord implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "机构编号")
	private Long org_id;
	@ApiModelProperty(value = "仓库内部编号")
	private Long warehouse_inner_sn;
	@Excel(name = "仓库名称")
	@ApiModelProperty(value = "仓库名称")
	private String warehouse_name;
	@ApiModelProperty(value = "厂商内部编号")
	private Long product_vendor_inner_sn;
	@Excel(name = "厂商/经销商")
	@ApiModelProperty(value = "厂商/经销商")
	private String product_vendor_name;
	@ApiModelProperty(value = "产品内部编号")
	private Long product_inner_sn;
	@Excel(name = "产品名称")
	@ApiModelProperty(value = "产品名称")
	private String product_name;
	@ApiModelProperty(value = "型号规格内部编号")
	private Long specification_inner_sn;
	@Excel(name = "型号规格")
	@ApiModelProperty(value = "型号规格")
	private String specification;
	@Excel(name = "单位")
	@ApiModelProperty(value = "单位")
	private String unit;
	@Excel(name = "仓库人员")
	@ApiModelProperty(value = "仓库人员")
	private String employee_name;
	@Excel(name = "成本均价", cellType = ColumnType.NUMERIC, scale = 4)
	@ApiModelProperty(value = "成本均价")
	private BigDecimal unit_price = BigDecimal.ZERO;
	@Excel(name = "数量", cellType = ColumnType.NUMERIC, scale = 2)
	@ApiModelProperty(value = "数量")
	private BigDecimal quantity = BigDecimal.ZERO;
	@Excel(name = "库存金额", cellType = ColumnType.NUMERIC, scale = 4)
	@ApiModelProperty(value = "库存金额")
    private BigDecimal amount_of_money = BigDecimal.ZERO;
	@Excel(name = "失效日期", dateFormat = "yyyy-MM-dd")
	@ApiModelProperty(value = "失效日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date overdue_date;
	@Excel(name = "批号")
	@ApiModelProperty(value = "批号")
	private String batch_number;
	
	@ApiModelProperty(value = "产品类型 0:经销商(自建)产品   1:厂商产品")
	private Integer product_type;
}
