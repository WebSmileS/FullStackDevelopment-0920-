package com.yrt.project.api.platform.vo.organization;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.utils.StringUtils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 前端添加厂商医疗器械生产许可证信息
 * 
 * @author Runner
 *
 */
@Data
@ApiModel(value="前端添加厂商医疗器械生产许可证信息",description="前端添加厂商医疗器械生产许可证信息")
public class AddVendorMDPLForm implements Serializable {

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
	
	
	/** 截止日期 */
	@ApiModelProperty(value="医疗器械生产许可证有效日期",required=true)
	@JsonFormat(pattern = "yyyy-MM-dd")
	@NotNull(message="有效日期不能为空")
	private Date end_date;
	
	/** 颁发日期 */
	@ApiModelProperty(value="医疗器械生产许可证发证日期",required=true)
	@JsonFormat(pattern = "yyyy-MM-dd")
	@NotNull(message="发证日期不能为空")
	private Date issue_date;
	
	/** 许可证编号 */
	@ApiModelProperty(value="医疗器械生产许可证许可证编号",required=false)
	private String license_sn = "";
	
	/** 生产地址 */
	@ApiModelProperty(value="医疗器械生产许可证生产地址",required=false)
	private String production_address = "";
	
	/** 发证部门 */
	@ApiModelProperty(value="医疗器械生产许可证发证部门",required=false)
	private String issue_organization = "";
	
	/** 生产范围 */
	@ApiModelProperty(value="医疗器械生产许可证生产范围，多个68分类用英文逗号','分割",required=false)
	private String production_range;
	
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
