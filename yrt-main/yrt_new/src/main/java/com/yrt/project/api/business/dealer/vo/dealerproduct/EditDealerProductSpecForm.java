package com.yrt.project.api.business.dealer.vo.dealerproduct;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EditDealerProductSpecForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="机构ID",required=true)
	@NotNull(message = "机构不能为空")
	private Long oid;
	@ApiModelProperty(value="产品ID",required=true)
	@NotNull(message = "产品不能为空")
	private Long pid;
	@ApiModelProperty(value="规格型号ID",required=true)
	@NotNull(message = "规格型号ID不能为空")
	private Long psid;
	@ApiModelProperty(value="规格型号",required=true)
	@NotBlank(message = "规格型号不能为空")
	private String spec;
	@ApiModelProperty(value="状态 0-无效 1-有效",required=true)
	@NotNull(message = "状态不能为空")
	private Integer status;

}