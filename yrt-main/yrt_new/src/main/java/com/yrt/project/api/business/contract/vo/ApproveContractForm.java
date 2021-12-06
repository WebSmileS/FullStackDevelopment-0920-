package com.yrt.project.api.business.contract.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ApproveContractForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 甲方ID */
	@ApiModelProperty(value="甲方ID",required=true)
	@NotNull(message = "甲方ID不能为空")
	private Long partA;
	
	/** 乙方ID */
	@ApiModelProperty(value="乙方ID",required=true)
	@NotNull(message = "乙方ID不能为空")
	private Long partB;
	
	/** 合同类型  0 hospitalDealer 1 hospitalVendor 2 dealerVendor 3 dealerDealer*/
	@ApiModelProperty(value="合同类型  0 hospitalDealer 1 hospitalVendor 2 dealerVendor 3 dealerDealer ",required=true)
	@NotNull(message = "合同类型不能为空")
	private Integer contract_type;
	
	/** 经销商经销商合同内部编码 */
	@ApiModelProperty(value="合同ID ",required=true)
	@NotNull(message = "合同ID不能为空")
	private Long contract_inner_sn;
	
	/** 合同审批内部编号 */
	@ApiModelProperty(value="合同审批内部编号 ",required=true)
	@NotNull(message = "合同审批ID不能为空")
	private Long approve_inner_sn;
	
	/** 合同审批内部编号 */
	@ApiModelProperty(value="合同审批结论 1 同意 2不同意 3 待定  ",required=true)
	@NotNull(message = "合同审批结论ID不能为空")
	private Integer status;
	
	/** 合同审批内部编号 */
	@ApiModelProperty(value="合同审批意见 ",required=false)
	private String description = "";

}
