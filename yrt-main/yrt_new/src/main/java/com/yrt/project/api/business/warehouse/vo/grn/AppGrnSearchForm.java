package com.yrt.project.api.business.warehouse.vo.grn;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.yrt.project.api.app.search.vo.AppSearchForm;

import io.swagger.annotations.ApiModelProperty;

public class AppGrnSearchForm extends AppSearchForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="状态(可选1-待入库 2-已入库 3-全部)",required=true)
	@NotNull(message = "状态不能为空!")
	private Integer status;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
