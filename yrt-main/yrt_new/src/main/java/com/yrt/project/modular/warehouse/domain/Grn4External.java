package com.yrt.project.modular.warehouse.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Grn4External implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "入库单内部编号")
	private Long grn_inner_sn;
	@ApiModelProperty(value = "相关机构内部编号")
	private Long organization_inner_sn;
	@ApiModelProperty(value = "相关机构名称")
	private String organization_name;
	@ApiModelProperty(value = "审核人名称")
	private String auditor_name;
	@ApiModelProperty(value = "仓库内部编号")
	private Long warehouse_inner_sn;
	@ApiModelProperty(value = "仓库名称")
	private String warehouse_name;
	@ApiModelProperty(value = "经手人名称")
	private String handler_name;
	@ApiModelProperty(value = "制单人名称")
	private String making_people_name;
	@ApiModelProperty(value = "仓库人员名称")
	private String warehouse_operator_name;
	@ApiModelProperty(value = "类型内部编号")
	private Long type_inner_sn;
	@ApiModelProperty(value = "类型名称")
	private String type_name;
	@ApiModelProperty(value = "单据生成时间")
	@JSONField(format="yyyy-MM-dd HH:mm:ss")
	private Date in_time;
	@ApiModelProperty(value = "入库单编号")
	private String grn_sn;
	@ApiModelProperty(value = "描述")
	private String description;
	
	private List<GrnDetail4External> details;

}
