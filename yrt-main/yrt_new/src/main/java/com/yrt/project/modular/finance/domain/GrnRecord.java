package com.yrt.project.modular.finance.domain;

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
public class GrnRecord implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "机构内部编号")
	private Long org_id;
	@ApiModelProperty(value = "入库单内部编号")
	private Long grn_inner_sn;
	@Excel(name = "入库时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "入库时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date in_time;
	@Excel(name = "入库单编号")
	@ApiModelProperty(value = "入库单编号")
	private String grn_sn;
	@Excel(name = "描述")
	@ApiModelProperty(value = "描述")
	private String description;
	@Excel(name = "相关机构")
	@ApiModelProperty(value = "相关机构名称")
	private String organization_name;
	@Excel(name = "仓库")
	@ApiModelProperty(value = "仓库内部编号")
	private String warehouse_name;
	@Excel(name = "经手人")
	@ApiModelProperty(value = "经手人名称")
	private String handler_name;
	@Excel(name = "类型名称")
	@ApiModelProperty(value = "类型名称")
	private String type_name;
	@Excel(name = "部门")
	@ApiModelProperty(value = "开单部门名称")
	private String department_name;
	@ApiModelProperty(value="单据类型")
    private BillsType billsType;
	@ApiModelProperty(value = "仓库内部编号")
    private Long warehouse_inner_sn;
	@Excel(name = "厂商名称")
	@ApiModelProperty(value = "厂商名称")
    private String product_vendor_name;
	@Excel(name = "产品名称")
	@ApiModelProperty(value = "产品名称")
    private String product_name;
	@Excel(name = "型号规格")
	@ApiModelProperty(value = "型号规格")
    private String specification;
	@ApiModelProperty(value = "类别")
    private Integer type;
	@Excel(name = "批号")
	@ApiModelProperty(value = "批号")
    private String batch_number;
	@Excel(name = "采购价", cellType = ColumnType.NUMERIC, scale = 4)
	@ApiModelProperty(value = "采购价")
    private BigDecimal unit_price = BigDecimal.ZERO;
	@Excel(name = "数量", cellType = ColumnType.NUMERIC, scale = 3)
	@ApiModelProperty(value = "数量")
    private BigDecimal quantity = BigDecimal.ZERO;
	@Excel(name = "单位")
	@ApiModelProperty(value = "单位")
    private String unit_name;
	@Excel(name = "金额", cellType = ColumnType.NUMERIC, scale = 4)
	@ApiModelProperty(value = "金额")
    private BigDecimal amount_of_money = BigDecimal.ZERO;
	@Excel(name = "税率", cellType = ColumnType.NUMERIC, scale = 2)
	@ApiModelProperty(value = "税率")
	private BigDecimal rate = BigDecimal.ZERO;
    @ApiModelProperty(value = "产品类型 0:经销商(自建)产品   1:厂商产品")
    private Integer product_type;
    @Excel(name = "注册证")
	@ApiModelProperty(value = "注册证")
    private String mdrf_sn;
    @Excel(name = "采购员")
    @ApiModelProperty(value = "采购员")
    private String buyer_name;
}
