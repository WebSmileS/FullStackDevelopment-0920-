package com.yrt.project.modular.contract.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ContractApproveComment implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 甲方ID */
	private Long partA;
	/** 乙方ID */
	private Long partB;
	/** 合同内部编号 */
	private Long contract_inner_sn;
	/** 审批内部编号 */
	private Long approve_inner_sn;
	/** 评论内部编号 */
	private Long comment_inner_sn;
	/** 评论时间 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date comment_time;
	/** 评论 */
	private String comment;
}
