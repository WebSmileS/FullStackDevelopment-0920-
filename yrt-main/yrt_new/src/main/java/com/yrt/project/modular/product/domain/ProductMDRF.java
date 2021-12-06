package com.yrt.project.modular.product.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProductMDRF implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "厂商内部编号 ")
	private Long vendor_inner_sn;
	@ApiModelProperty(value = "厂商名称")
	private String vendor_name;
	@ApiModelProperty(value = "厂商地址")
	private String vendor_address;
	@ApiModelProperty(value = "产品内部编号")
	private Long product_inner_sn;
	@ApiModelProperty(value = "产品名称")
	private String product_name;
	@ApiModelProperty(value = "登记表内部编号")
	private Long mdrf_inner_sn;
	@ApiModelProperty(value = "颁发日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date issue_date;
	@ApiModelProperty(value = "开始日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date begin_date;
	@ApiModelProperty(value = "截止日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date;
	@ApiModelProperty(value = "注册号")
	private String register_sn;
	@ApiModelProperty(value = "发证部门")
	private String issue_organization;
	@ApiModelProperty(value = "产品标准")
	private String standard;
	@ApiModelProperty(value = "生产地址")
	private String production_address;
	@ApiModelProperty(value = "性能结构及组成")
	private String performance_structure;
	@ApiModelProperty(value = "适用范围")
	private String application_range;
	@ApiModelProperty(value = "禁忌症")
	private String contraindication;
	@ApiModelProperty(value = "描述")
	private String description;
	@ApiModelProperty(value = "型号规格(总览)")
	private String specifications;
	@ApiModelProperty(value = "是否存在注册证图片")
	private Boolean hasCertImage;
}
