package com.yrt.project.modular.product.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="前端展示产品相关图片信息",description="前端展示产品相关图片信息")
public class ProductRelImage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 厂商内部编号 */
	@ApiModelProperty(value="厂商内部编号")
	private Long vendor_inner_sn;
	/** 产品内部编号 */
	@ApiModelProperty(value="产品内部编号")
	private Long product_inner_sn;
	/** 图片内部编号 */
	@ApiModelProperty(value="图片内部编号")
	private Long image_inner_sn;
	/** 类别 */
	@ApiModelProperty(value="类别")
	private Integer type;
	/** 开始日期 */
	@ApiModelProperty(value="开始日期 ")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date begin_date;
	/** 截止日期 */
	@ApiModelProperty(value="截止日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date;
	/** 统一资源定位符 */
	@ApiModelProperty(value="统一资源定位符")
	private String url;

}
