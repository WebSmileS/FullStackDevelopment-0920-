package com.yrt.project.modular.warehouse.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WarehouseModel implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "机构编号")
	private Long org_id;
	@ApiModelProperty(value = "医院名称")
	private String org_name;
	@ApiModelProperty(value = "仓库模型内部编号")
	private Long warehouse_model_inner_sn;
	@ApiModelProperty(value = "上级仓库模型内部编号")
	private Long parent_warehouse_model_inner_sn;
	@ApiModelProperty(value = "仓库类别内部编号")
	private Long warehouse_type_inner_sn;
	@ApiModelProperty(value = "行政区划代码ID")
	private Long administrative_division_sn;
	@ApiModelProperty(value = "名称")
	private String name;
	@ApiModelProperty(value = "仓库模型编号")
	private String warehouse_model_sn;
	@ApiModelProperty(value = "类别")
	private Integer type;
	@ApiModelProperty(value = "状态")
	private Integer status;
	@ApiModelProperty(value = "面积(区域专用)")
	private BigDecimal proportion;
	@ApiModelProperty(value = "通道号(货架专用)")
	private String aisle_sn;
	@ApiModelProperty(value = "电话(仓库专用)")
	private String phone;
	@ApiModelProperty(value = "条码")
	private String barcode;
	@ApiModelProperty(value = "地址(仓库专用)")
	private String address;
	@ApiModelProperty(value = "描述")
	private String description;
	@ApiModelProperty(value = "部门内部编号组，用','分割")
	private String departments;
	@ApiModelProperty(value = "部门名称组，用','分割")
	private String department_names;
	@ApiModelProperty(value = "是否存在子节点 1-有 0-没有")
	private Integer is_leaf;
	@ApiModelProperty(value = "仓库相关人员列表")
	private List<WarehouseRelEmployee> employees;
	@ApiModelProperty(value = "上级仓库信息")
	private WarehouseModel parents;
	@ApiModelProperty(value = "下级仓库列表")
	private List<WarehouseModel> children;
}
