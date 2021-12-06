package com.yrt.project.modular.product.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="前端展示产品相关图片信息",description="前端展示产品相关图片信息")
public class DealerProductRelImage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="经销商内部编号")
	private Long dealer_inner_sn;
	@ApiModelProperty(value="产品内部编号")
	private Long product_inner_sn;
	@ApiModelProperty(value="图片内部编号")
	private Long image_inner_sn;
	@ApiModelProperty(value="类别")
	private Integer type;
	@ApiModelProperty(value="开始日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date begin_date;
	@ApiModelProperty(value="截止日期 ")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date;
	@ApiModelProperty(value="统一资源定位符")
	private String url;

}
