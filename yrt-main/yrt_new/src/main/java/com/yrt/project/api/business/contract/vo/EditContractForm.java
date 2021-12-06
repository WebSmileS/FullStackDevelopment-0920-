package com.yrt.project.api.business.contract.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.utils.StringUtils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EditContractForm implements Serializable  {
 
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
//	@ApiModelProperty(value = "销售代表内部编号", required = false)
//	private Long employee_inner_sn;
//	private Long employee_inner_sn_old;
	
	/** 销售代表名称 */
	@ApiModelProperty(value = "销售代表名称", required = false)
	private String employee_name;
	private String employee_name_old;
	
	/** 回款周期 */
	@ApiModelProperty(value = "回款周期号", required = false)
	private Long payment_period;
	private Long payment_period_old;

	/** 开始日期 */
	@ApiModelProperty(value = "开始日期", required = false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date begin_date;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date begin_date_old;
	
	/** 截止日期 */
	@ApiModelProperty(value = "截止日期", required = false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date_old;
	
	/** 签订日期 */
	@ApiModelProperty(value = "签订日期", required = false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date sign_date;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date sign_date_old;
	
	/** 名称 */
	@ApiModelProperty(value = "名称", required = false)
	private String name;
	private String name_old;
	
	/** 合同编号 */
//	@ApiModelProperty(value = "合同编号", required = false)
//	private String contract_sn;
//	private String contract_sn_old;
	
	/** 内容 */
	@ApiModelProperty(value = "内容", required = false)
	private String content;
	private String content_old;
	
	/** 内容 */
	@ApiModelProperty(value = "医院部门内部编号（逗号分隔）", required = false)
	private String hospital_department_inner_sn;
	private String hospital_department_inner_sn_old;
	
	@ApiModelProperty(value = "合同商品列表", required = false)
	private List<ContractProductForm> productList = new ArrayList<ContractProductForm>();
	
	@ApiModelProperty(value="合同附件列表",required = false)
	private List<ContractAttachmentForm> attachmentList = new ArrayList<ContractAttachmentForm>();
	
	@ApiModelProperty(value="合同ID",required=true)
	@NotNull(message = "合同ID不能为空")
	private Long contract_inner_sn;
	@ApiModelProperty(value="不需要前端输入",hidden=true)
	private Integer status;
	
	@ApiModelProperty(hidden = true)
	private Map<String,Object> info = new HashMap<String,Object>();
	
	@ApiModelProperty(hidden = true)
	public boolean isUpdate() {
		if((StringUtils.isNotEmpty(employee_name) || StringUtils.isNotEmpty(employee_name_old))) {
//			if(StringUtils.isNull(employee_inner_sn)) {
//				employee_inner_sn = -1L;
//				employee_name = "";
//			}
//			info.put("employee_inner_sn", employee_inner_sn);
			info.put("employee_name", employee_name);
		}
		if((StringUtils.isNotNull(payment_period) || StringUtils.isNotNull(payment_period_old))) {
			info.put("payment_period", payment_period);
		}
		if((StringUtils.isNotNull(begin_date) || StringUtils.isNotNull(begin_date_old))) {
			info.put("begin_date", begin_date);
		}
		if((StringUtils.isNotNull(end_date) || StringUtils.isNotNull(end_date_old))) {
			info.put("end_date", end_date);
		}
		if((StringUtils.isNotNull(sign_date) || StringUtils.isNotNull(sign_date_old))) {
			if(StringUtils.isNull(sign_date)) {
				info.put("sign_date_null", 1);
			}
			info.put("sign_date", sign_date);
		}
//		if((StringUtils.isNotNull(hospital_department_inner_sn) || StringUtils.isNotNull(hospital_department_inner_sn_old))) {
//			info.put("hospital_department_inner_sn", hospital_department_inner_sn);
//		}
		if(StringUtils.isNotEmpty(name) || StringUtils.isNotEmpty(name_old)) {
			info.put("name", name);
		}
//		if(StringUtils.isNotEmpty(contract_sn) || StringUtils.isNotEmpty(contract_sn_old)) {
//			info.put("contract_sn", contract_sn);
//		}
		if(StringUtils.isNotEmpty(content) || StringUtils.isNotEmpty(content_old)) {
			info.put("content", content);
		}
		if(StringUtils.isNotNull(status)) {
			info.put("status", status);
		}
		return StringUtils.isNotEmpty(info);
	}
	@ApiModelProperty(hidden = true)
	public Map<String,Object> getInfo(){
		return info;
	}
	
}
