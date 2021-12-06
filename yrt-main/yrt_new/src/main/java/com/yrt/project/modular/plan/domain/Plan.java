package com.yrt.project.modular.plan.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Plan implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "机构内部编号")
	private Long org_id;
	@ApiModelProperty(value = "采购计划单内部编号")
	private Long ppl_inner_sn;
	@ApiModelProperty(value = "审核人内部编号")
	private Long auditor_inner_sn;
	@ApiModelProperty(value = "审核人名称")
	private String auditor_name;
	@ApiModelProperty(value = "制单人内部编号")
	private Long creator_inner_sn;
	@ApiModelProperty(value = "制单人名称")
	private String creator_name;
	@ApiModelProperty(value = "部门内部编号")
	private Long department_inner_sn;
	@ApiModelProperty(value = "部门名称")
	private String department_name;
	@ApiModelProperty(value = "计划生成时间 指正式计划的生成时间 ")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date create_time;
	@ApiModelProperty(value = "计划审核时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date audit_time;
	@ApiModelProperty(value = "状态  0-草稿 1-正式 2-已审核 3-已终止 4-订单已生成 ")
	private Integer status;
	@ApiModelProperty(value = "采购计划单编号")
	private String ppl_sn;
	@ApiModelProperty(value = "描述")
	private String description;
	@ApiModelProperty(value = "采购计划明细列表")
	private List<PlanDetail> detailList = new ArrayList<>();
}
