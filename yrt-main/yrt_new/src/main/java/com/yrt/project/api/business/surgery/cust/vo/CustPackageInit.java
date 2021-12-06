package com.yrt.project.api.business.surgery.cust.vo;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 定制 手术包/工具包 初始化
 */
@Data
public class CustPackageInit {
	
	/**
	 * create a new one with different src_id
	 * @param src_id
	 * @return
	 */
	public CustPackageInit copyWithNewSrcId(long src_id) {
		CustPackageInit ret = new CustPackageInit();
		ret.org_id = this.org_id;
		ret.org_name = this.org_name;
		ret.src_id = src_id;
		return ret;
	}

	@NotNull
	@ApiModelProperty(value="当前经销商id", required=true)
	private Long org_id;
	
	@NotNull
	@ApiModelProperty(value="当前经销商名称", required=true)
	private String org_name;
	
	@NotNull
	@ApiModelProperty(value="平台 源(手术包/工具包)id", required=true)
	private Long src_id;
	
}
