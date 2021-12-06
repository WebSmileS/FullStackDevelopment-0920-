package com.yrt.project.modular.contract.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.project.api.business.contract.vo.ContractAttachmentForm;
import com.yrt.project.api.business.contract.vo.ContractProductForm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Contract implements Serializable {

private static final long serialVersionUID = 1L;
		
	@ApiModelProperty(value = "甲方内部编号")
	private Long partA;
	@ApiModelProperty(value = "甲方名称")
	private String partA_name;
	@ApiModelProperty(value = "甲方地址")
	private String partA_address;
	@ApiModelProperty(value = "甲方联系电话")
	private String partA_phone;
	@ApiModelProperty(value = "乙方内部编号")
	private Long partB;
	@ApiModelProperty(value = "乙方名称")
	private String partB_name;
	@ApiModelProperty(value = "乙方地址")
	private String partB_address;
	@ApiModelProperty(value = "乙方联系电话")
	private String partB_phone;
	@ApiModelProperty(value = "合同类型  0 hospitalDealer 1 hospitalVendor 2 dealerVendor 3 dealerDealer")
	private Integer contract_type;
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
	@ApiModelProperty(value = "医院部门内部编码（逗号分隔）")
	private String hospital_dept_inner_sn;
	@ApiModelProperty(value = "医院部门名称（逗号分隔）")
	private String hospital_dept_name;
	
	private List<ContractAttachment> attachments;
	private List<ContractVendorProduct> details;
	
	private List<ContractProductForm> productList;
	private List<ContractAttachmentForm> attachmentList;
}
