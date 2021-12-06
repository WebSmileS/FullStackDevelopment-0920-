package com.yrt.project.api.business.order.vo.record;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AppSaleRecordSearchForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="机构ID",required=true)
	@NotNull(message = "机构ID不能为空")
	private Long oid;
	@ApiModelProperty(value="搜索关键字",required=false)
	private String key;
}
