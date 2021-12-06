package com.yrt.project.api.business.plan.vo;

import java.io.Serializable;

import com.yrt.project.api.app.search.vo.AppSearchForm;

import io.swagger.annotations.ApiModelProperty;

public class AppPlanSearchForm extends AppSearchForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="状态(待执行可选0-待处理 1-历史 2-全部)",required=true)
	private Integer status;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
