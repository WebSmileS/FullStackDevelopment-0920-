package com.yrt.project.api.business.applyvoucher.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AppGrantConfirmForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="机构ID",required=true)
	@NotNull(message = "机构ID不能为空")
	private Long oid;
	@ApiModelProperty(value="发放单ID",required=true)
	@NotNull(message = "发放单ID不能为空")
	private Long gid;
	@ApiModelProperty(value="确认发放明细列表",required=true)
	private List<AppGrantConfirmDetail> details = new ArrayList<>();
}
