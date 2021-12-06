package com.yrt.project.api.business.contract.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class ContractApproveForm implements Serializable{
	
private static final long serialVersionUID = 1L;
	
	/** 经销商a内部编码 */
	private Long partA;
	
	/** 经销商b内部编码 */
	private Long partB;
	
	/** 合同内部编号 */
	private Long contract_inner_sn;
	
	/** 合同审批内部编号 */
	private Long approve_inner_sn;
	
	/** 合同审批的前一个审批内部编号 */
	private Long previous_approve_inner_sn;
		
	/** 审批状态 */
	private Integer status;
	
	/**审批人user_id */
	private Long approver_inner_sn;
	
	/**审批人名称 */
	private String approver_name;
	
	/**审批时间 */
	private Date approve_time;
	
	/**审批描述*/
	private String description;
	
	/**审批评论 */
	private List<ContractApproveCommentForm> approveComment = new ArrayList<>();
	
}
