package com.yrt.project.api.platform.vo.organization;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.utils.StringUtils;
import com.yrt.project.modular.organization.domain.OrgRelImage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 前端展示和修改医院医疗机构执业许可证信息
 * 
 * @author Runner
 *
 */
@Data
@ApiModel(value="前端展示和修改医院医疗机构执业许可证信息",description="前端展示和修改医院医疗机构执业许可证信息")
public class HospitalPLHMIForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="不需要前端输入",hidden=true)
	private Map<String, Object> orgInfo = new HashMap<String, Object>();
	@ApiModelProperty(value="不需要前端输入",hidden=true)
	private Map<String, Object> hPLHMI = new HashMap<String, Object>();
	
	@ApiModelProperty(value="不需要前端输入",hidden=true)
	private boolean isNew = false;
	
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
	@ApiModelProperty(value="医疗机构执业许可证登记号",required=false)
	private String registered_number;
	private String registered_number_old;
	
	/** 开始日期 */
	@ApiModelProperty(value="医疗机构执业许可证开始日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date begin_date;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date begin_date_old;
	
	/** 截止日期 */
	@ApiModelProperty(value="医疗机构执业许可证截止日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date_old;
	
	/** 诊疗科目 */
	@ApiModelProperty(value="医疗机构执业许可证诊疗科目",required=false)
	private String diagnosis_subject;
	private String diagnosis_subject_old;
	
	/** 发证机关 */
	@ApiModelProperty(value="医疗机构执业许可证发证机关",required=false)
	private String issue_organization;
	private String issue_organization_old;
	
	/** 颁发日期 */
	@ApiModelProperty(value="医疗机构执业许可证颁发日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date issue_date;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date issue_date_old;
	
	/** 相关图片 */
	@ApiModelProperty(value="不需要前端输入",hidden=true)
	private List<OrgRelImage> images;
	
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
	
	public boolean hasHospitalPLHMI() {
		if(StringUtils.isNotEmpty(registered_number) || StringUtils.isNotEmpty(registered_number_old)) {
			if(StringUtils.isEmpty(registered_number)) {
				registered_number = "";
			}
			hPLHMI.put("registered_number", registered_number);
		}
		if(StringUtils.isNotNull(begin_date) || StringUtils.isNotNull(begin_date_old)) {
			hPLHMI.put("begin_date", begin_date);
		}
		if(StringUtils.isNotNull(end_date) || StringUtils.isNotNull(end_date_old)) {
			hPLHMI.put("end_date", end_date);
		}
		if(StringUtils.isNotEmpty(diagnosis_subject) || StringUtils.isNotEmpty(diagnosis_subject_old)) {
			if(StringUtils.isEmpty(diagnosis_subject)) {
				diagnosis_subject = "";
			}
			hPLHMI.put("diagnosis_subject", diagnosis_subject);
		}
		if(StringUtils.isNotEmpty(issue_organization) || StringUtils.isNotEmpty(issue_organization_old)) {
			if(StringUtils.isEmpty(issue_organization)) {
				issue_organization = "";
			}
			hPLHMI.put("issue_organization", issue_organization);
		}
		if(StringUtils.isNotNull(issue_date) || StringUtils.isNotNull(issue_date_old)) {
			hPLHMI.put("issue_date", issue_date);
		}
		return StringUtils.isNotEmpty(hPLHMI);
	}
	
	public Map<String, Object> getHospitalPLHMI(){
		return hPLHMI;
	}
}
