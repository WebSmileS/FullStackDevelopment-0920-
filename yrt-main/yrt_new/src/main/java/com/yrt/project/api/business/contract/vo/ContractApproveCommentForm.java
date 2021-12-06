package com.yrt.project.api.business.contract.vo;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class ContractApproveCommentForm implements Serializable {
	 	
	private static final long serialVersionUID = 1L;
	
	private Long comment_inner_sn;
	
	private String comment;
	
	private Date comment_time;

}
