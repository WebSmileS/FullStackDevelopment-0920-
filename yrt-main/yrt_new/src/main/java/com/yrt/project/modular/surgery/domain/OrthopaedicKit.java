package com.yrt.project.modular.surgery.domain;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrthopaedicKit implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "工具包内部编号")
	private Long kit_inner_sn;
	@ApiModelProperty(value = "类别内部编号")
	private Long type_inner_sn;
	@ApiModelProperty(value = "类别名称")
	private String type_name;
	@ApiModelProperty(value = "品牌内部编号")
	private Long brand_inner_sn;
	@ApiModelProperty(value = "品牌名称")
	private String brand_name;
	@ApiModelProperty(value = "状态")
	private Integer status;
	@ApiModelProperty(value = "工具包编号")
	private String kit_sn;
	@ApiModelProperty(value = "名称")
	private String name;
	@ApiModelProperty(value = "描述")
	private String description;
	@ApiModelProperty(value = "排序号")
	private Integer sort_number;
	
	@ApiModelProperty(value = "关键字")
	private String keywords;
	
	@ApiModelProperty(value = "工具包明细列表")
	private List<OrthopaedicKitDetail> details;
	
	
}
