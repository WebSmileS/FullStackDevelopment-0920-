package com.yrt.project.modular.warehouse.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GrnDetail4External implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "明细内部编号")
	private Long detail_inner_sn;
	
	@ApiModelProperty(value = "厂商内部编号 ")
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
	@ApiModelProperty(value = "产品单位名称")
	private String unit;
	@ApiModelProperty(value = "登记表内部编号")
	private Long mdrf_inner_sn;
	@ApiModelProperty(value = "医疗器械注册证号")
	private String mdrf_sn;
	@ApiModelProperty(value = "数量")
	private BigDecimal quantity;
	@ApiModelProperty(value = "税率")
	private BigDecimal rate;
	@ApiModelProperty(value = "生产日期")
	@JSONField(format="yyyy-MM-dd")
	private Date produce_date;
	@ApiModelProperty(value = "失效日期")
	@JSONField(format="yyyy-MM-dd")
	private Date overdue_date;
	@ApiModelProperty(value = "批号")
	private String batch_number;
	@ApiModelProperty(value = "产品类型 0:经销商(自建)产品   1:厂商产品")
	private Integer product_type;
	@ApiModelProperty(value = "单价")
	private BigDecimal unit_price;
	@ApiModelProperty(value = "医保编号")
	private String health_care_sn;
}
