package com.yrt.project.modular.area.domain;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 行政区划
 * @author Runner
 *
 */
@Data
public class AdministrativeDivision implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 行政区划ID */
	private Long administrative_division_sn;
	/** 行政区划父ID */
	private Long parent_administrative_division_sn;
	/** 名称 */
	private String name;
	/** 状态 0-无效 1-有效 */
	private Integer status;
	/** 是否存在子节点 0-有 1-没有 */
	@ApiModelProperty(value="是否存在子节点 0-有 1-没有",hidden = true)
	private Integer is_leaf;
	
	private AdministrativeDivision parent;

}
