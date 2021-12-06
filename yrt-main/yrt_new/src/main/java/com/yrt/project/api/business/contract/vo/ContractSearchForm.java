package com.yrt.project.api.business.contract.vo;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ContractSearchForm implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message="当前机构ID不能为空")
	private Long partA;
	@ApiModelProperty(value="乙方ID",hidden=true)
	private Long partB;
	
	@ApiModelProperty(value="甲方名称",hidden=false)
	private String partA_name;
	@ApiModelProperty(value="乙方名称",hidden=false)
	private String partB_name;
	
	/** 销售代表 */
	@ApiModelProperty(value="销售代表",required=false)
	private Integer employee_inner_sn;
	
	/** 合同拟定人 */
	@ApiModelProperty(value="合同拟定人",required=false)
	private Long inputer_inner_sn;
	
	/** 状态 */
	@ApiModelProperty(value="合同状态(多个状态用逗号分隔)",required=false)
	private String status = "3,4,5,6,7";
	
	/** 合同编号 */
	@ApiModelProperty(value="合同编号",required=false)
	private String contract_sn;
	
	/** 合同名称 */
	@ApiModelProperty(value="合同名称",required=false)
	private String name;
 
	/** 合同生效日期晚于 */
	@ApiModelProperty(value="合同生效日期晚于",required=false)
	private Date begin_date_s;
	
	/** 合同生效日期早于 */
	@ApiModelProperty(value="合同生效日期早于",required=false)
	private Date begin_date_e;
	
	/** 合同失效日期晚于 */
	@ApiModelProperty(value="合同失效日期晚于",required=false)
	private Date end_date_s;
	
	/** 合同失效日期早于 */
	@ApiModelProperty(value="合同失效日期早于",required=false)
	private Date end_date_e;
	
	
	/** 合同签订日期晚于 */
	@ApiModelProperty(value="合同签订日期晚于",required=false)
	private Date sign_date_s;
	
	/** 合同签订日期早于 */
	@ApiModelProperty(value="合同签订日期早于",required=false)
	private Date sign_date_e;
}
