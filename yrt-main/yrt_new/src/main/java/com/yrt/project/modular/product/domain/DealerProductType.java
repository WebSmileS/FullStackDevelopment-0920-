package com.yrt.project.modular.product.domain;

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
public class DealerProductType implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 经销商内部编号 */
	@ApiModelProperty(value="经销商内部编号",required=true)
	private Long dealer_inner_sn;
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
	@ApiModelProperty(value="是否是子节点 1-有 0-没有",required=false)
	private Integer is_leaf;
	@ApiModelProperty(value="自定义分类描述")
	private DealerProductType parent;
	private List<DealerProductType> children;
}
