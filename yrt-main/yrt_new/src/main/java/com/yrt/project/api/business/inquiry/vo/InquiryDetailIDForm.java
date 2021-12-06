package com.yrt.project.api.business.inquiry.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class InquiryDetailIDForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 询价机构 */
	@ApiModelProperty(value="询价机构ID",required=true)
	@NotNull(message = "询价机构ID不能为空")
	private Long partA;
	
	/** 询价单内部编号 */
	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long ilps_inner_sn;
	
	@ApiModelProperty(value="询价明细ID",required=true)
	@NotNull(message = "询价明细ID不能为空")
	private Long detail_inner_sn;
	
	@ApiModelProperty(value="单据类型",required=true)
	@NotNull(message = "单据类型不能为空")
	private BillsType billsType;
}
