package com.yrt.project.api.business.dealer.vo.tmpproduct;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AuditForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value="数据来源 0来自平台;1来自医院;2来自经销商;3来自厂商",required=true)
	@NotNull(message = "数据来源不能为空")
	private Integer source;
	
	@ApiModelProperty(value="厂商ID",required=true)
	@NotNull(message = "厂商ID不能为空")
	private Long vendor_inner_sn;
	
	@ApiModelProperty(value="产品ID",required=true)
	@NotNull(message = "产品ID不能为空")
	private Long product_inner_sn;
	
	@ApiModelProperty(value="是否待审核产品 0-不是 1-是",required=true)
	@NotNull(message = "是否待审核产品不能为空")
	private Integer is_tmp;
}
