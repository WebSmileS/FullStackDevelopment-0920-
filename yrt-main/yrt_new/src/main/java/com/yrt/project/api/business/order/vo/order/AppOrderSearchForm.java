package com.yrt.project.api.business.order.vo.order;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AppOrderSearchForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="机构ID",required=true)
	@NotNull(message = "机构ID不能为空")
	private Long oid;
	@ApiModelProperty(value="状态传值可选: -1-全部 0-草稿 1-正式 2-已审批 3-确认 4-已终止 5-已完结 6-未通过 ",required=true)
	@NotNull(message = "状态传值不能为空")
	private Integer status;
	@ApiModelProperty(value="搜索关键字",required=false)
	private String key;

}
