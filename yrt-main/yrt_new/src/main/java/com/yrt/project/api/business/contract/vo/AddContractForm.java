package com.yrt.project.api.business.contract.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddContractForm implements Serializable  {
 
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
	
	/** 销售代表内部编号 */
//	@NotNull(message="销售代表不能为空")
//	@ApiModelProperty(value = "销售代表内部编号",required = true)
//	private Long employee_inner_sn;
	
	/** 销售代表名称 */
//	@NotBlank(message="销售代表名称不能为空")
	@ApiModelProperty(value = "销售代表名称", required = true)
	private String employee_name = "";
	
	/** 回款周期 */
	@NotNull(message="回款周期号不能为空")
	@ApiModelProperty(value = "回款周期号", required = true)
	private Long payment_period;

	/** 开始日期 */
	@NotNull(message="开始日期不能为空")
	@ApiModelProperty(value = "开始日期", required = true)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date begin_date;
	
	/** 截止日期 */
	@NotNull(message="截止日期不能为空")
	@ApiModelProperty(value = "截止日期", required = true)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date;
	
	/** 签订日期 */
	@ApiModelProperty(value = "签订日期", required = false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date sign_date;
	
	/** 名称 */
	@NotBlank(message="名称不能为空")
	@ApiModelProperty(value = "名称", required = true)
	private String name;
	
	/** 合同编号 */
//	@NotBlank(message="合同编号不能为空")
	@ApiModelProperty(value = "合同编号", required = false)
	private String contract_sn;
	
	/** 内容 */
	@ApiModelProperty(value = "内容", required = false)
	private String content;
	
	/** 内容 */
	@ApiModelProperty(value = "医院部门内部编号（逗号分隔）", required = false)
	private String hospital_department_inner_sn;
	
	@ApiModelProperty(value = "合同商品列表", required = false)
	private List<ContractProductForm> productList = new ArrayList<ContractProductForm>();
	
	@ApiModelProperty(value="合同附件列表",required = false)
	private List<ContractAttachmentForm> attachmentList = new ArrayList<ContractAttachmentForm>();
	
}
