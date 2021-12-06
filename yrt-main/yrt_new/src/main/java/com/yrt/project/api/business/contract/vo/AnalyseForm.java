package com.yrt.project.api.business.contract.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AnalyseForm implements Serializable{
	
 
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="甲方ID",required=true)
	private Long partA;
	
	@ApiModelProperty(value="乙方ID",required=true)
	private Long partB;
	
	/** 合同类型  0 hospitalDealer 1 hospitalVendor 2 dealerVendor 3 dealerDealer*/
	@ApiModelProperty(value="合同类型  0-hospitalDealer 1-hospitalVendor",required=true)
	private Integer contract_type;
	
	/** 销售代表内部编号 */
	@NotNull(message="销售代表不能为空")
	@ApiModelProperty(value = "销售代表内部编号", required = true)
	private Long employee_inner_sn;
	
	@ApiModelProperty(value = "医院部门内部编号（逗号分隔），如果空则代表全院所有部门", required = false)
	private String hospital_department_inner_sn = "";
	
	private List<AnalyseDetailForm> details = new ArrayList<>();
	
	@ApiModelProperty(hidden = true)
	public boolean canAnalyse() {
		if(contract_type != 0 || contract_type != 1) {
			return false;
		}
		return true;
	}

}
