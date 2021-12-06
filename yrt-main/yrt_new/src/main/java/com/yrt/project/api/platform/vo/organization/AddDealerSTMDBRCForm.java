package com.yrt.project.api.platform.vo.organization;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.utils.StringUtils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 前端添加经销商第二类医疗器械经营备案凭证信息
 * 
 * @author Runner
 *
 */
@Data
@ApiModel(value="前端添加经销商第二类医疗器械经营备案凭证信息",description="前端添加经销商第二类医疗器械经营备案凭证信息")
public class AddDealerSTMDBRCForm implements Serializable {
	
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
	
	
	/** 备案编号 */
	@ApiModelProperty(value="第二类医疗器械经营备案凭证备案编号",required=true)
	@NotBlank(message="备案编号不能为空")
	private String record_sn;
	
	/** 经营方式 */
	@ApiModelProperty(value="第二类医疗器械经营备案凭证经营方式",required=true)
	@NotNull(message="经营方式不能为空")
	private Integer operation_mode;
	
	/** 经营场所 */
	@ApiModelProperty(value="第二类医疗器械经营备案凭证经营场所",required=false)
	private String premises = "";
	
	/** 库房地址 */
	@ApiModelProperty(value="第二类医疗器械经营备案凭证库房地址",required=false)
	private String warehouse_address = "";
	
	/** 备案日期 */
	@ApiModelProperty(value="第二类医疗器械经营备案凭证备案日期",required=true)
	@JsonFormat(pattern = "yyyy-MM-dd")
	@NotNull(message="备案日期不能为空")
	private Date issue_date;
	
	/** 发证部门 */
	@ApiModelProperty(value="第二类医疗器械经营备案凭证发证部门",required=false)
	private String issue_organization = "";
	
	/** 经营范围 */
	@ApiModelProperty(value="第二类医疗器械经营备案凭证经营范围，多个68分类用英文逗号','分割",required=false)
	private String business_scope;
	
	
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
