package com.yrt.project.modular.surgery.domain;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrthopaedicPackage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "手术包内部编号")
	private Long ois_inner_sn;
	@ApiModelProperty(value = "类别内部编号")
	private Long type_inner_sn;
	@ApiModelProperty(value = "类别名称")
	private String type_name;
	@ApiModelProperty(value = "状态")
	private Integer status;
	@ApiModelProperty(value = "手术包名称")
	private String name;
	@ApiModelProperty(value = "描述")
	private String description;
	@ApiModelProperty(value = "品牌内部编号")
	private Long brand_inner_sn;
	@ApiModelProperty(value = "品牌名称")
	private String brand_name;
	@ApiModelProperty(value = "编号")
	private String ois_sn;
	
	@ApiModelProperty(value = "关键字")
	private String keywords;
	
	@ApiModelProperty(value = "手术包明细列表")
	private List<OrthopaedicPackageDetail> details;
	@ApiModelProperty(value = "手术包工具明细列表")
	private List<OrthopaedicKit> kits;
}
