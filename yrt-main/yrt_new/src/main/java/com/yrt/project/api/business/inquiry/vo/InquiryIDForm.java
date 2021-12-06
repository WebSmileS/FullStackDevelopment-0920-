package com.yrt.project.api.business.inquiry.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class InquiryIDForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 询价机构 */
	@ApiModelProperty(value="询价机构ID",required=true)
	@NotNull(message = "询价机构ID不能为空")
	private Long partA;
	/** 询价单内部编号 */
	@ApiModelProperty(value="询价单内ID",required=true)
	@NotNull(message = "询价单内ID不能为空")
	private Long ilps_inner_sn;
	
	@ApiModelProperty(value="单据类型",required=true)
	@NotNull(message = "单据类型不能为空")
	private BillsType billsType;
	
	public InquiryIDForm() {
		
	}
	public InquiryIDForm(Long partA, Long ilps_inner_sn, BillsType billsType) {
		this.partA = partA;
		this.ilps_inner_sn = ilps_inner_sn;
		this.billsType = billsType;
	}
}
