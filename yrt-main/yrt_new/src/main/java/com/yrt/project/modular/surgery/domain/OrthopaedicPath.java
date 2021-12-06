package com.yrt.project.modular.surgery.domain;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrthopaedicPath implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "手术径路内部编号")
	private Long op_inner_sn;
	@ApiModelProperty(value = "上级手术径路内部编号")
	private Long parent_op_inner_sn;
	@ApiModelProperty(value = "名称")
	private String name;
	
	@ApiModelProperty(value = "是否存在子节点1-有 0-没有")
	private Integer is_leaf;
	
	@ApiModelProperty(value = "手术包列表")
	private List<OrthopaedicPackage> packages;
	@ApiModelProperty(value = "上级路径")
	private OrthopaedicPath topParent;
}
