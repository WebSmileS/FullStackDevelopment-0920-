package com.yrt.project.modular.contract.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ContractApprove implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 经销商a内部编号 */
	private Long partA;
	/** 经销商b内部编号 */
	private Long partB;
	/** 合同内部编号 */
	private Long contract_inner_sn;
	/** 审批内部编号 */
	private Long approve_inner_sn;
	/** 当前处理人员内部编号(审批者/修改者) */
	private Long current_handler_inner_sn;
	/** 下一处理人员内部编号(审批者/修改者) */
	private Long next_handler_inner_sn;
	/** 上一审批内部编号 */
	private Long previous_approve_inner_sn;
	/** 状态 */
	private Integer status;
	/** 处理时间 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date dispose_time;
	/** 描述 */
	private String description;

}
