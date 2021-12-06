package com.yrt.project.api.business.contract.vo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ContractForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	
	@ApiModelProperty(value = "合同类型  0 hospitalDealer 1 hospitalVendor 2 dealerVendor 3 dealerDealer")
	private Integer contract_type;
	@ApiModelProperty(value = "甲方内部编号")
	private Long partA;
	@ApiModelProperty(value = "甲方名称")
	private String partA_name;
	@ApiModelProperty(value = "乙方内部编号")
	private Long partB;
	@ApiModelProperty(value = "乙方名称")
	private String partB_name;
	@ApiModelProperty(value = "合同内部编号")
	private Long contract_inner_sn;
	@ApiModelProperty(value = "销售代表内部编号")
	private Long employee_inner_sn;
	@ApiModelProperty(value = "销售代表名字")
	private String employee_name;
	@ApiModelProperty(value = "录入人员内部编号")
	private Long inputer_inner_sn;
	@ApiModelProperty(value = "录入人员名字")
	private String inputer_name;
	@ApiModelProperty(value = "回款周期")
	private Long payment_period;
	@ApiModelProperty(value = "合同状态")
	private Integer status;
	@ApiModelProperty(value = "开始日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date begin_date;
	@ApiModelProperty(value = "截止日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date;
	@ApiModelProperty(value = "签订日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date sign_date;
	@ApiModelProperty(value = "合同名称")
	private String name;
	@ApiModelProperty(value = "合同编号")
	private String contract_sn;
	@ApiModelProperty(value = "内容 ")
	private String content;
	@ApiModelProperty(value = "当前审批人内部编号")
	private Long approver_inner_sn;
	@ApiModelProperty(value = "当前审批人")
	private String approver_name;
}
