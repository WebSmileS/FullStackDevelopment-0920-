package com.yrt.project.api.business.contract.vo;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ApproverForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="合同发起人、审批人ID",required=true)
	@NotNull(message = "合同发起人或者审批人ID不能为空")
	private Long approver_inner_sn;
	
	@ApiModelProperty(value="合同发起人、审批人名称",required=true)
	@NotBlank(message = "合同发起人或者审批人名称不能为空")
	private String approver_name;
}
