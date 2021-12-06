package com.yrt.project.modular.organization.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PlatformDept implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "部门内部编号")
	private Long department_inner_sn;
	@ApiModelProperty(value = "上级部门内部编号")
	private Long parent_department_inner_sn;
	@ApiModelProperty(value = "主管员工内部编号")
	private Long director_inner_sn;
	@ApiModelProperty(value = "主管员工名称")
	private String director_name;
	@ApiModelProperty(value = "状态 ")
	private Integer status;
	@ApiModelProperty(value = "名称")
	private String name;
	@ApiModelProperty(value = "部门编号")
	private String department_sn;
	@ApiModelProperty(value = "电话 ")
	private String phone;
	@ApiModelProperty(value = "描述")
	private String description;
	@ApiModelProperty(value = "是否存在子节点 1-有 0-没有")
	private Integer is_leaf;
	@ApiModelProperty(value = "上级部门信息")
	private PlatformDept parent;
	@ApiModelProperty(value = "下级部门列表")
	private List<PlatformDept> children = new ArrayList<>();

}
