package com.yrt.project.api.platform.vo.product;

import java.io.Serializable;

import com.yrt.common.utils.sql.SqlUtil;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="前端搜索产品信息",description="前端搜索产品信息")
public class SearchProductMDRFForm implements Serializable {

	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="厂商名称",required=false)
	private String vendor_name;
	
	@ApiModelProperty(value="产品名称",required=false)
	private String product_name;
	
	@ApiModelProperty(value="注册证编号",required=false)
	private String register_sn;
	
	@ApiModelProperty(value="是否有注册证图片0-全部 1-有 2-没有 (默认0-全部)",required=false)
	private Integer hasCertImage = 0;

	public SearchProductMDRFForm escapeLikeValue() {
		vendor_name = SqlUtil.escapeLikeValue(vendor_name);
		product_name = SqlUtil.escapeLikeValue(product_name);
		register_sn = SqlUtil.escapeLikeValue(register_sn);
		return this;
	}
}
