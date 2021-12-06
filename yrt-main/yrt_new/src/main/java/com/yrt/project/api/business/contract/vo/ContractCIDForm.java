package com.yrt.project.api.business.contract.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ContractCIDForm implements Serializable{
	
 
	private static final long serialVersionUID = 1L;
	
	/** 甲方ID */
	@ApiModelProperty(value="甲方ID",required=true)
	private Long partA;
	
	/** 乙方ID */
	@ApiModelProperty(value="乙方ID",required=true)
	private Long partB;
	
	/** 经销商经销商合同内部编码 */
	@ApiModelProperty(value="合同ID ",required=true)
	private Long contract_inner_sn;
	
	/** 合同类型  0 hospitalDealer 1 hospitalVendor 2 dealerVendor 3 dealerDealer*/
	@ApiModelProperty(value="合同类型  0 hospitalDealer 1 hospitalVendor 2 dealerVendor 3 dealerDealer ",required=true)
	private Integer contract_type;

}
