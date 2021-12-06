package com.yrt.project.api.platform.vo.product;

import java.io.Serializable;

import com.yrt.common.utils.StringUtils;

import io.swagger.annotations.ApiModelProperty;

public class MatchBean implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "属性名称")
	private String property_name;
	@ApiModelProperty(value = "新属性值")
	private String new_value;
	@ApiModelProperty(value = "旧属性值")
	private String old_value;
	@ApiModelProperty(value = "是否匹配")
	private boolean match;
	
	public MatchBean(String property_name, String new_value, String old_value) {
		super();
		this.property_name = property_name;
		this.new_value = StringUtils.isNotEmpty(new_value)?new_value:"";
		this.old_value = StringUtils.isNotEmpty(old_value)?old_value:"";
		if(this.new_value.equalsIgnoreCase(this.old_value)) {
			this.match = true;
		}else {
			this.match = false;
		}
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
	public boolean isMatch() {
		return match;
	}
	public void setMatch(boolean match) {
		this.match = match;
	}
	
	
}
