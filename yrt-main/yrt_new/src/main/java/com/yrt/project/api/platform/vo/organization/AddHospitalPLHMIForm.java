package com.yrt.project.api.platform.vo.organization;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.yrt.common.utils.StringUtils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 前端添加医院医疗机构执业许可证信息
 * 
 * @author Runner
 *
 */
@Data
@ApiModel(value="前端添加医院医疗机构执业许可证信息",description="前端添加医院医疗机构执业许可证信息")
public class AddHospitalPLHMIForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="不需要前端输入",hidden=true)
	private Map<String, Object> orgInfo = new HashMap<String, Object>();
	
	/** 名称* */
	@ApiModelProperty(value="机构信息名称",required=false)
	private String name;
	@ApiModelProperty(value="原机构信息名称",required=false)
	private String name_old;
	
	/** 法定代表人 */
	@ApiModelProperty(value="机构信息法定代表人",required=false)
	private String legal_representative;
	@ApiModelProperty(value="原机构信息法定代表人",required=false)
	private String legal_representative_old;
	
	/** 企业负责人 */
	@ApiModelProperty(value="机构信息企业负责人",required=false)
	private String principal;
	@ApiModelProperty(value="原机构信息企业负责人",required=false)
	private String principal_old;
	
	/** 地址 */
	@ApiModelProperty(value="机构信息地址",required=false)
	private String address;
	@ApiModelProperty(value="原机构信息地址",required=false)
	private String address_old;
	
	
	/** 登记号 */
	@ApiModelProperty(value="医疗机构执业许可证登记号",required=true)
	@NotBlank(message="登记号不能为空")
	private String registered_number;
	
	/** 开始日期 */
	@ApiModelProperty(value="医疗机构执业许可证开始日期",required=true)
	@NotNull(message="开始日期不能为空")
	private Date begin_date;
	
	/** 截止日期 */
	@ApiModelProperty(value="医疗机构执业许可证截止日期",required=true)
	@NotNull(message="截止日期不能为空")
	private Date end_date;
	
	/** 诊疗科目 */
	@ApiModelProperty(value="医疗机构执业许可证诊疗科目",required=false)
	private String diagnosis_subject = "";
	
	/** 发证机关 */
	@ApiModelProperty(value="医疗机构执业许可证发证机关",required=false)
	private String issue_organization;
	
	/** 颁发日期 */
	@ApiModelProperty(value="医疗机构执业许可证颁发日期",required=true)
	@NotNull(message="颁发日期不能为空")
	private Date issue_date;
	
	
	public boolean hasOrgInfo() {
		if(StringUtils.isNotEmpty(name) || StringUtils.isNotEmpty(name_old)) {
			orgInfo.put("name", name);
		}
		if(StringUtils.isNotEmpty(legal_representative) || StringUtils.isNotEmpty(legal_representative_old)) {
			if(StringUtils.isEmpty(legal_representative)) {
				legal_representative = "";
			}
			orgInfo.put("legal_representative", legal_representative);
		}
		if(StringUtils.isNotEmpty(principal) || StringUtils.isNotEmpty(principal_old)) {
			if(StringUtils.isEmpty(principal)) {
				principal = "";
			}
			orgInfo.put("principal", principal);
		}
		if(StringUtils.isNotEmpty(address) || StringUtils.isNotEmpty(address_old)) {
			if(StringUtils.isEmpty(address)) {
				address = "";
			}
			orgInfo.put("address", address);
		}
		return StringUtils.isNotEmpty(orgInfo);
	}

	public Map<String, Object> getOrgInfo(){
		return orgInfo;
	}
}
