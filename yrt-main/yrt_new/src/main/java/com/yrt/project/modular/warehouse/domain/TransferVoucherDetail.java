package com.yrt.project.modular.warehouse.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.TransferVoucherStatus;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TransferVoucherDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "调单单据内部编号")
	private Long voucher_inner_sn;
	@ApiModelProperty(value = "调单单据明细内部编号")
	private Long detail_inner_sn;
	@ApiModelProperty(value = "厂商内部编号如果是经销商自建产品则是经销商ID")
	private Long product_vendor_inner_sn;
	@ApiModelProperty(value = "厂商名称")
	private String product_vendor_name;
	@ApiModelProperty(value = "产品内部编号")
	private Long product_inner_sn;
	@ApiModelProperty(value = "产品名称")
	private String product_name;
	@ApiModelProperty(value = "型号规格内部编号")
	private Long specification_inner_sn;
	@ApiModelProperty(value = "型号规格")
	private String specification;
	@ApiModelProperty(value = "单位内部编号")
	private Long unit_inner_sn;
	@ApiModelProperty(value = "单位名称")
	private String unit;
	@ApiModelProperty(value = "数量")
	private BigDecimal quantity;
	@ApiModelProperty(value = "税率")
	private BigDecimal rate;
	@ApiModelProperty(value = "单价")
	private BigDecimal unit_price;
	@ApiModelProperty(value = "排序号")
	private Integer sort_number;
	@ApiModelProperty(value = "批次号")
	private String batch_number;
	@ApiModelProperty(value = "产品类型 0:经销商(自建)产品   1:厂商产品")
	private Integer product_type;
	@ApiModelProperty(value = "注册证内部编号")
	private Long mdrf_inner_sn;
	@ApiModelProperty(value = "注册证编号")
	private String mdrf_sn;
	@ApiModelProperty(value = "生产日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private String produce_date;
	@ApiModelProperty(value = "失效日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private String overdue_date;
	@ApiModelProperty(value = "状态")
	private TransferVoucherStatus status;
	@ApiModelProperty(value = "医保编号")
	private String health_care_sn;
	@ApiModelProperty(value = "溢价比例")
	private BigDecimal percent;
	@ApiModelProperty(value = "售价")
	private BigDecimal salesPrice;
}
