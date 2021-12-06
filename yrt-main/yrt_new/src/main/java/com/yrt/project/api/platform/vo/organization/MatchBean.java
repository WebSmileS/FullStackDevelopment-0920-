package com.yrt.project.api.platform.vo.organization;

import java.io.Serializable;

import com.yrt.common.utils.StringUtils;

import io.swagger.annotations.ApiModelProperty;

public class MatchBean implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "属性名称")
	private String property_name;
	@ApiModelProperty(value = "属性描述")
	private String property_desc;
	@ApiModelProperty(value = "新注册属性值")
	private String new_value;
	@ApiModelProperty(value = "系统旧属性值")
	private String old_value;
	
	public MatchBean(String property_name, String property_desc, String new_value, String old_value) {
		super();
		this.property_name = property_name;
		this.property_desc = property_desc;
		this.new_value = StringUtils.isNotEmpty(new_value)?new_value:"";
		this.old_value = StringUtils.isNotEmpty(old_value)?old_value:"";
	}
	
	public String getProperty_name() {
		return property_name;
	}
	public void setProperty_name(String property_name) {
		this.property_name = property_name;
	}
	public String getNew_value() {
		return new_value;
	}
	public void setNew_value(String new_value) {
		this.new_value = new_value;
	}
	public String getOld_value() {
		return old_value;
	}
	public void setOld_value(String old_value) {
		this.old_value = old_value;
	}

	public String getProperty_desc() {
		return property_desc;
	}

	public void setProperty_desc(String property_desc) {
		this.property_desc = property_desc;
	}
}
