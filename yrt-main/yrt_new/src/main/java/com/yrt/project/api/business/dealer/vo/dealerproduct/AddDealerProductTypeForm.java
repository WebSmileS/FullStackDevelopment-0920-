package com.yrt.project.api.business.dealer.vo.dealerproduct;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddDealerProductTypeForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="上级类型ID",required=false)
	private Long parent_type_inner_sn;
	@ApiModelProperty(value="类型名称",required=true)
	@NotBlank(message = "类型名称不能为空")
	private String type;
	@ApiModelProperty(value="类型描述",required=false)
	private String description = "";
}
