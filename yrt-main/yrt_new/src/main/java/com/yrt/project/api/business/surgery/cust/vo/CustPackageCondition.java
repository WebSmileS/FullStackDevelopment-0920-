package com.yrt.project.api.business.surgery.cust.vo;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CustPackageCondition {
	
	@ApiModelProperty(value = "查询文本 (name/keywords like)", required=false)
	private String q;
	
	@ApiModelProperty(value = "手术包 编号", required=false)
	private String ois_sn;
	
	@ApiModelProperty(value = "手术类型inner_sn(数组)", required=false)
	private List<Byte> types;
	
	@ApiModelProperty(value = "品牌类型inner_sn(数组)", required=false)
	private List<Short> brands;
	
	@ApiModelProperty(value = "查询范围(默认所有)", required=false)
	private SearchScope searchScope = SearchScope.ALL;
	
	/**
	 * 经销商 按路径查看 手术包/ 医生按路径选手术包
	 */
	@ApiModelProperty(value = "手术路径inner_sn(经销商路径查看界面)", required=false)
	private Long op_inner_sn;
	
}
