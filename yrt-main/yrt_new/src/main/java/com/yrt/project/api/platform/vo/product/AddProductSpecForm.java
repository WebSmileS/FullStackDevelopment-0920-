package com.yrt.project.api.platform.vo.product;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddProductSpecForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="机构ID",required=true)
	@NotNull(message = "机构不能为空")
	private Long oid;
	@ApiModelProperty(value="产品ID",required=true)
	@NotNull(message = "产品不能为空")
	private Long pid;
	@ApiModelProperty(value="规格型号",required=true)
	@NotNull(message = "规格型号不能为空")
	private String spec;
	@ApiModelProperty(value="编码",required=false)
	private String pvd;
	@ApiModelProperty(value="状态 0-无效 1-有效",required=true)
	@NotNull(message = "状态不能为空")
	private Integer status;
	
	@ApiModelProperty(value = "医保编号")
	private String default_health_care_sn;
	@ApiModelProperty(value = "阳光流水号")
	private String default_sun_sn;
}
