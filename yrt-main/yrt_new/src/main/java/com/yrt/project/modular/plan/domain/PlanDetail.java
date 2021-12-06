package com.yrt.project.modular.plan.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PlanDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "机构内部编号")
	private Long org_id;
	@ApiModelProperty(value = "采购计划单内部编号")
	private Long ppl_inner_sn;
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
	@ApiModelProperty(value = "批号")
	private String batch_number;
	@ApiModelProperty(value = "排序号")
	private Long sort_number;
	@ApiModelProperty(value = "状态 0-待处理 1-已终止 2-已生成采购订单 ")
	private Integer status;
	@ApiModelProperty(value = "产品类型 0:经销商(自建)产品   1:厂商产品")
	private Integer product_type;
}
