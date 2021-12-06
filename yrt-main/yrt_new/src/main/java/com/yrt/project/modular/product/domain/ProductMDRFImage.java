package com.yrt.project.modular.product.domain;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="产品医疗器械注册登记表相关图片",description="产品医疗器械注册登记表相关图片")
public class ProductMDRFImage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 厂商内部编号 */
	@ApiModelProperty(value="厂商内部编号")
	private Long vendor_inner_sn;
	/** 产品内部编号 */
	@ApiModelProperty(value="产品内部编号")
	private Long product_inner_sn;
	/** 登记表内部编号 */
	@ApiModelProperty(value="登记表内部编号")
	private Long mdrf_inner_sn;
	/** 图片内部编号 */
	@ApiModelProperty(value="图片内部编号")
	private Long image_inner_sn;
	/** 统一资源定位符 */
	@ApiModelProperty(value="统一资源定位符")
	private String url;
	@ApiModelProperty(value="文件名")
	private String file_name;
}
