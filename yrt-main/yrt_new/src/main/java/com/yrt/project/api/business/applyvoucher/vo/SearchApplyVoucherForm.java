package com.yrt.project.api.business.applyvoucher.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchApplyVoucherForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="医院ID",required=true)
	@NotNull(message = "医院不能为空")
	private Long hospital_inner_sn;
	
	@ApiModelProperty(value="状态 0-草稿 1-申领 2-全部发放(自然终止) 3-申领终止(人为终止)",required=false)
	private String status;
	
	@ApiModelProperty(value="单据编号",required=false)
	private String voucher_sn;
	
	@ApiModelProperty(value="申领人名称",required=false)
	private String proposer_name;
	
	@ApiModelProperty(value="部门ID",required=false)
	private String department_inner_sn;
	
}
