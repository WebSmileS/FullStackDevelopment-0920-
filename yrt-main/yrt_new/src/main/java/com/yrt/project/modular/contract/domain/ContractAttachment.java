package com.yrt.project.modular.contract.domain;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ContractAttachment implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "甲方内部编号")
	private Long partA;
 
	@ApiModelProperty(value = "乙方内部编号")
	private Long partB;
 
	@ApiModelProperty(value = "合同内部编号")
	private Long contract_inner_sn;
	 
	/** 合同附件内部编号 */
	@ApiModelProperty(value = "合同附件内部编号")
	private Long attachment_inner_sn;
	
	/** 合同附件名称 */
	@ApiModelProperty(value = "合同附件名称")
	private String attachment_name;
	
	/** 合同附件URL */
	@ApiModelProperty(value = "合同附件URL")
	private String attachment_url;

}
