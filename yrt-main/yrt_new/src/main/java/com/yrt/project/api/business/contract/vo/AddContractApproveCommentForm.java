package com.yrt.project.api.business.contract.vo;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddContractApproveCommentForm implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/** 甲方ID */
	@ApiModelProperty(value="甲方ID",required=true)
	@NotNull(message = "甲方ID不能为空")
	private Long partA;
	
	/** 乙方ID */
	@ApiModelProperty(value="乙方ID",required=true)
	@NotNull(message = "乙方ID不能为空")
	private Long partB;
	
	/** 经销商经销商合同内部编码 */
	@ApiModelProperty(value="合同ID ",required=true)
	@NotNull(message = "合同ID不能为空")
	private Long contract_inner_sn;
	
	/** 合同审批内部编号 */
	@NotNull(message = "合同审批ID不能为空")
	@ApiModelProperty(value="合同审批内部编码",required=true)
	private Long approve_inner_sn;
	
	/** 合同审批内部编号 */
	@ApiModelProperty(value="合同审批备注",required=true)
	@NotBlank(message = "合同审批备注不能为空")
	private String comment;
	
	/** 合同类型  0 hospitalDealer 1 hospitalVendor 2 dealerVendor 3 dealerDealer*/
	@ApiModelProperty(value="合同类型  0 hospitalDealer 1 hospitalVendor 2 dealerVendor 3 dealerDealer ",required=true)
	@NotNull(message = "合同类型不能为空")
	private Integer contract_type;
 
}
