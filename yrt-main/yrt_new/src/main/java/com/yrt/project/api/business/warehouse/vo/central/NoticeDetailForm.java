package com.yrt.project.api.business.warehouse.vo.central;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NoticeDetailForm {

	@ApiModelProperty(value="产品厂商ID")
	private Long product_vendor_inner_sn;
	@ApiModelProperty(value="产品ID")
	private Long product_inner_sn;
	@ApiModelProperty(value="规格型号ID")
	private Long specification_inner_sn;
	@ApiModelProperty(value="单位ID")
	private Long unit_inner_sn;
	@ApiModelProperty(value="注册证ID,只有平台产品有")
	private Long mdrf_inner_sn;
	@ApiModelProperty(value="产品厂商名称")
	private String product_vendor_name;
	@ApiModelProperty(value="产品名称")
	private String product_name;
	@ApiModelProperty(value="规格型号")
	private String specification;
	@ApiModelProperty(value="单位名称")
	private String unit;
	@ApiModelProperty(value="注册证号,只有平台产品有")
	private String mdrf_sn;
	@ApiModelProperty(value="批号")
	private String batch_number;
	@ApiModelProperty(value="单价")
	private BigDecimal unit_price;
	@ApiModelProperty(value="数量")
	private BigDecimal quantity;
	@ApiModelProperty(value="税率")
	private BigDecimal rate;
	@ApiModelProperty(value="生产日期")
	private Date produce_date;
	@ApiModelProperty(value="过期日期")
	private Date overdue_date;
	@ApiModelProperty(value="产品类型 0-自建产品 1-平台产品")
	private Integer product_type;
}
