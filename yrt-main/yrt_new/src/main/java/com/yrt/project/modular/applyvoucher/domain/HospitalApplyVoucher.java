package com.yrt.project.modular.applyvoucher.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class HospitalApplyVoucher implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "医院内部编号")
	private Long hospital_inner_sn;
	@ApiModelProperty(value = "医院名称")
	private String hospital_name;
	@ApiModelProperty(value = "单据内部编号")
	private Long voucher_inner_sn;
	@ApiModelProperty(value = "单据编号")
	private String voucher_sn;
	@ApiModelProperty(value = "申领人内部编号")
	private Long proposer_inner_sn;
	@ApiModelProperty(value = "申领人名称")
	private String proposer_name;
	@ApiModelProperty(value = "部门内部编号")
	private Long department_inner_sn;
	@ApiModelProperty(value = "部门名称")
	private String department_name;
	@ApiModelProperty(value = "单据生成时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date create_time;
	@ApiModelProperty(value = "期望发放日期(之前)")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date expect_date;
	@ApiModelProperty(value = "状态 0-草稿 1-申领 2-部分发放 3-全部发放(自然终止) 4-申领终止(人为终止)")
	private Integer status;
	@ApiModelProperty(value = "描述")
	private String description;
	@ApiModelProperty(value = "明细条目数")
	private Long detail_count;

	@ApiModelProperty(value="仓库内部编号",notes = "可为空")
	private Long warehouse_inner_sn;

	@ApiModelProperty(value="仓库名称",notes = "可为空")
	private String warehouse_inner_sn_name;
}
