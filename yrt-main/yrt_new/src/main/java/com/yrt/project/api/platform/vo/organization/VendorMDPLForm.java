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
 * 前端展示和修改厂商医疗器械生产许可证信息
 * 
 * @author Runner
 *
 */
@Data
@ApiModel(value="前端展示和修改厂商医疗器械生产许可证信息",description="前端展示和修改厂商医疗器械生产许可证信息")
public class VendorMDPLForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="不需要前端输入",hidden=true)
	private Map<String, Object> orgInfo = new HashMap<String, Object>();
	@ApiModelProperty(value="不需要前端输入",hidden=true)
	private Map<String, Object> vMDPL = new HashMap<String, Object>();

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
	
	
	/** 截止日期 */
	@ApiModelProperty(value="医疗器械生产许可证截止日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date_old;
	
	/** 颁发日期 */
	@ApiModelProperty(value="医疗器械生产许可证颁发日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date issue_date;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date issue_date_old;
	
	/** 许可证编号 */
	@ApiModelProperty(value="医疗器械生产许可证许可证编号",required=false)
	private String license_sn;
	private String license_sn_old;
	
	/** 生产地址 */
	@ApiModelProperty(value="医疗器械生产许可证生产地址",required=false)
	private String production_address;
	private String production_address_old;
	
	/** 发证部门 */
	@ApiModelProperty(value="医疗器械生产许可证发证部门",required=false)
	private String issue_organization;
	private String issue_organization_old;
	
	/** 生产范围 */
	@ApiModelProperty(value="医疗器械生产许可证生产范围，多个68分类用英文逗号','分割",required=false)
	private String production_range;
	private String production_range_old;
	
	@ApiModelProperty(value="不需要前端输入",hidden=true)
	private String production_range_name;
	
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
	
	public boolean hasDealerMDPL() {
		if(StringUtils.isNotEmpty(license_sn) || StringUtils.isNotEmpty(license_sn_old)) {
			if(StringUtils.isEmpty(license_sn)) {
				license_sn = "";
			}
			vMDPL.put("license_sn", license_sn);
		}
		if(StringUtils.isNotEmpty(production_address) || StringUtils.isNotEmpty(production_address_old)) {
			if(StringUtils.isEmpty(production_address)) {
				production_address = "";
			}
			vMDPL.put("production_address", production_address);
		}
		/*
		 * if(StringUtils.isNotEmpty(production_range) ||
		 * StringUtils.isNotEmpty(production_range_old)) { vMDPL.put("production_range",
		 * production_range); }
		 */
		if(StringUtils.isNotNull(end_date) || StringUtils.isNotNull(end_date_old)) {
			vMDPL.put("end_date", end_date);
		}
		if(StringUtils.isNotNull(issue_date) || StringUtils.isNotNull(issue_date_old)) {
			vMDPL.put("issue_date", issue_date);
		}
		if(StringUtils.isNotEmpty(issue_organization) || StringUtils.isNotEmpty(issue_organization_old)) {
			if(StringUtils.isEmpty(issue_organization)) {
				issue_organization = "";
			}
			vMDPL.put("issue_organization", issue_organization);
		}
		return StringUtils.isNotEmpty(vMDPL);
	}
	
	public Map<String, Object> getDealerMDPL(){
		return vMDPL;
	}
}
