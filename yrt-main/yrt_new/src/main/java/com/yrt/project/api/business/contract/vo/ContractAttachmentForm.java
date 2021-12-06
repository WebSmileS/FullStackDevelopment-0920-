package com.yrt.project.api.business.contract.vo;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ContractAttachmentForm implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/** 合同附件名称 */
	@ApiModelProperty(value = "合同附件名称", required = true)
	@NotBlank(message = "合同附件名称不能为空")
	private String attachment_name;
	
	/** 合同附件URL */
	@ApiModelProperty(value = "合同附件URL", required = true)
	@NotBlank(message = "附件URL不能为空")
	private String attachment_url;
	
}
