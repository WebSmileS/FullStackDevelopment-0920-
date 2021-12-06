package com.yrt.project.api.business.surgery.cust.vo;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CustKitCondition {
	@ApiModelProperty(value = "查询文本 (name/keywords like)", required=false)
	private String q;
	
	@ApiModelProperty(value = "工具包 编号", required=false)
	private String kit_sn;
	
	@ApiModelProperty(value = "手术类型inner_sn数组(多选)", required=false)
	private List<Byte> types;
	
	@ApiModelProperty(value = "品牌类型inner_sn数组(多选)", required=false)
	private List<Short> brands;
	
	@ApiModelProperty(value = "查询范围(默认所有)", required=false)
	private SearchScope searchScope = SearchScope.ALL;
	
}
