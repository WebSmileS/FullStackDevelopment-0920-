package com.yrt.project.modular.order.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrderDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "采购方机构编号")
	private Long partA;
	@ApiModelProperty(value = "采购方机构名称")
	private String partA_name;
	@ApiModelProperty(value = "销售方机构编号")
	private Long partB;
	@ApiModelProperty(value = "销售方机构名称")
	private String partB_name;
	@ApiModelProperty(value = "订单内部编号")
	private Long po_so_inner_sn;
	@ApiModelProperty(value = "明细内部编号")
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
	private String unit_name;
	@ApiModelProperty(value = "数量")
	private BigDecimal quantity;
	@ApiModelProperty(value = "含税单价")
	private BigDecimal unit_price;
	@ApiModelProperty(value = "折扣")
	private BigDecimal discount;
	@ApiModelProperty(value = "税率")
	private BigDecimal rate;
	@ApiModelProperty(value = "排序号")
	private Integer sort_number;
	@ApiModelProperty(value = "状态 0-待处理 1-已终止 2-已完结")
	private Integer status;
	@ApiModelProperty(value = "产品类型 0:经销商(自建)产品   1:厂商产品")
	private Integer product_type;
	@ApiModelProperty(value = "批次号")
	private String batch_number;

	@ApiModelProperty(value = "注册证号")
	private String newest_register_sn;

	/** 注册证ID */
//	private Long mdrf_inner_sn;
	/** 注册证号 */
//	private String mdrf_sn;
	/** 生产日期 */
//	@JsonFormat(pattern = "yyyy-MM-dd")
//	private Date produce_date;
	/** 过期日期 */
//	@JsonFormat(pattern = "yyyy-MM-dd")
//	private Date overdue_date;
}
