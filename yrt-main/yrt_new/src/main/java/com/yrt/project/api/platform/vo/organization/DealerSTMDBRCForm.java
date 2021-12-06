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
 * 前端展示和修改经销商第二类医疗器械经营备案凭证信息
 * 
 * @author Runner
 *
 */
@Data
@ApiModel(value="前端展示和修改经销商第二类医疗器械经营备案凭证信息",description="前端展示和修改经销商第二类医疗器械经营备案凭证信息")
public class DealerSTMDBRCForm implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="不需要前端输入",hidden=true)
	private Map<String, Object> orgInfo = new HashMap<String, Object>();
	@ApiModelProperty(value="不需要前端输入",hidden=true)
	private Map<String, Object> dSTMDBRC = new HashMap<String, Object>();
	
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
	
	
	/** 备案编号 */
	@ApiModelProperty(value="第二类医疗器械经营备案凭证备案编号",required=false)
	private String record_sn;
	private String record_sn_old;
	
	/** 经营方式 */
	@ApiModelProperty(value="第二类医疗器械经营备案凭证经营方式",required=false)
	private Integer operation_mode;
	private Integer operation_mode_old;
	
	/** 经营场所 */
	@ApiModelProperty(value="第二类医疗器械经营备案凭证经营场所",required=false)
	private String premises;
	private String premises_old;
	
	/** 库房地址 */
	@ApiModelProperty(value="第二类医疗器械经营备案凭证库房地址",required=false)
	private String warehouse_address;
	private String warehouse_address_old;
	
	/** 备案日期 */
	@ApiModelProperty(value="第二类医疗器械经营备案凭证备案日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date issue_date;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date issue_date_old;
	
	/** 发证部门 */
	@ApiModelProperty(value="第二类医疗器械经营备案凭证发证部门",required=false)
	private String issue_organization;
	private String issue_organization_old;
	
	/** 经营范围 */
	@ApiModelProperty(value="第二类医疗器械经营备案凭证经营范围，多个68分类用英文逗号','分割",required=false)
	private String business_scope;
	private String business_scope_old;
	
	@ApiModelProperty(value="不需要前端输入",hidden=true)
	private String business_scope_name;
	
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
	
	public boolean hasDealerSTMDBRC() {
		if(StringUtils.isNotEmpty(record_sn) || StringUtils.isNotEmpty(record_sn_old)) {
			if(StringUtils.isEmpty(record_sn)) {
				record_sn = "";
			}
			dSTMDBRC.put("record_sn", record_sn);
		}
		if(StringUtils.isNotNull(operation_mode) || StringUtils.isNotNull(operation_mode_old)) {
			dSTMDBRC.put("operation_mode", operation_mode);
		}
		if(StringUtils.isNotEmpty(premises) || StringUtils.isNotEmpty(premises_old)) {
			if(StringUtils.isEmpty(premises)) {
				premises = "";
			}
			dSTMDBRC.put("premises", premises);
		}
		if(StringUtils.isNotEmpty(warehouse_address) || StringUtils.isNotEmpty(warehouse_address_old)) {
			if(StringUtils.isEmpty(warehouse_address)) {
				warehouse_address = "";
			}
			dSTMDBRC.put("warehouse_address", warehouse_address);
		}
		if(StringUtils.isNotNull(issue_date) || StringUtils.isNotNull(issue_date_old)) {
			dSTMDBRC.put("issue_date", issue_date);
		}
		if(StringUtils.isNotEmpty(issue_organization) || StringUtils.isNotEmpty(issue_organization_old)) {
			if(StringUtils.isEmpty(issue_organization)) {
				issue_organization = "";
			}
			dSTMDBRC.put("issue_organization", issue_organization);
		}
		/*
		 * if(StringUtils.isNotEmpty(business_scope) ||
		 * StringUtils.isNotEmpty(business_scope_old)) { dSTMDBRC.put("business_scope",
		 * business_scope); }
		 */
		return StringUtils.isNotEmpty(dSTMDBRC);
	}
	
	public Map<String, Object> getDealerSTMDBRC(){
		return dSTMDBRC;
	}
}
