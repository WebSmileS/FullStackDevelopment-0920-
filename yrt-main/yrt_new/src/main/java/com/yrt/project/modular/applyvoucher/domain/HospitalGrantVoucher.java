package com.yrt.project.modular.applyvoucher.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class HospitalGrantVoucher implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "医院内部编号")
	private Long hospital_inner_sn;
	@ApiModelProperty(value = "医院名称")
	private String hospital_name;
	@ApiModelProperty(value = "发放单内部编号")
	private Long gv_inner_sn;
	@ApiModelProperty(value = "部门内部编号")
	private Long department_inner_sn;
	/** 经手人内部编号 */
//	private Long handler_inner_sn;
	@ApiModelProperty(value = "批准人(发放人)内部编号")
	private Long approver_inner_sn;
	@ApiModelProperty(value = "领取人内部编号")
	private Long receiver_inner_sn;
	@ApiModelProperty(value = "已删除")
	private Integer is_delete;
	@ApiModelProperty(value = "状态 0-可发放 1-已出库 2-已领取")
	private Integer status;
	@ApiModelProperty(value = "发放时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date grant_time;
	@ApiModelProperty(value = "发放单编号")
	private String gv_sn;
	@ApiModelProperty(value = "描述")
	private String description;
	@ApiModelProperty(value = "部门名称")
	private String department_name;
	/** 经手人姓名(冗余) */
//	private String handler_name;
	@ApiModelProperty(value = "批准人(发放人)姓名")
	private String approver_name;
	@ApiModelProperty(value = "仓库内部编号")
	private Long warehouse_inner_sn;
	@ApiModelProperty(value = "仓库名称")
	private String warehouse_name;
	@ApiModelProperty(value = "领取人姓名(冗余)")
	private String receiver_name;

	@ApiModelProperty(value = "关联的明细列表")
	List<HospitalGrantVoucherDetail> details;
}
