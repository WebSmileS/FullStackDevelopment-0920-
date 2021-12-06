package com.yrt.project.modular.category.domain;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 医疗器械68分类 code68
 * 
 * @author Runner
 *
 */
@Data
public class Code68 implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "68编码")
	private Long code68_sn;
	@ApiModelProperty(value = "68父级编码")
	private Long parent_code68_sn;
	@ApiModelProperty(value = "68编码名称")
	private String name;
	@ApiModelProperty(value = "管理级别 1,2,3")
	private Integer management_type;
	@ApiModelProperty(value = "举例")
	private String example;
	@ApiModelProperty(value="是否存在子节点 1-有 0-没有")
	private Integer is_leaf;
	
	@ApiModelProperty(value="上级分类列表")
	private Code68 parent;
	
	@ApiModelProperty(value="子分类列表")
	private List<Code68> children;

}
