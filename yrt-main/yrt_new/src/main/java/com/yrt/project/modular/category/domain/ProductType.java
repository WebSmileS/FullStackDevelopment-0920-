package com.yrt.project.modular.category.domain;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 自定义分类 product_type
 * 
 * @author Runner
 */
@Data
public class ProductType implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 分类Id */
	@ApiModelProperty(value="自定义分类ID")
	private Long type_inner_sn;
	/** 父分类Id */
	@ApiModelProperty(value="自定义分类父ID",required=true)
	private Long parent_type_inner_sn;
	/** 状态 0-失效 1-有效 */
	@ApiModelProperty(value="状态 0-失效 1-有效",required=true)
	private Integer status;
	/** 分类 */
	@ApiModelProperty(value="自定义分类名称",required=true)
	private String type;
	/** 分类描述 */
	@ApiModelProperty(value="自定义分类描述",required=false)
	private String description = "";
	/** 是否存在子节点 0-有 1-没有 */
	@ApiModelProperty(value="是否存在子节点 1-有 0-没有")
	private Integer is_leaf;
	@ApiModelProperty(value="上级分类列表")
	private ProductType parent;
	@ApiModelProperty(value="子分类列表")
	private List<ProductType> children;
}
