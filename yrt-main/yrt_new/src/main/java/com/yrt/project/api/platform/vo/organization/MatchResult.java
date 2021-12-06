package com.yrt.project.api.platform.vo.organization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.yrt.common.utils.StringUtils;

import io.swagger.annotations.ApiModelProperty;


public class MatchResult implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "属性列表")
	private List<MatchBean> list = new ArrayList<>();
	@ApiModelProperty(value = "系统存在的机构ID")
	private Long orgId;
	@ApiModelProperty(value = "系统存在的管理员账号(不需要在页面中显示)")
	private String account;
	
	public List<MatchBean> getList() {
		return list;
	}
	public void setList(List<MatchBean> list) {
		this.list = list;
	}
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public void addList(MatchBean bean) {
		this.list.add(bean);
	}
	
	public boolean isEmpty() {
		return StringUtils.isEmpty(list);
	}
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
}
