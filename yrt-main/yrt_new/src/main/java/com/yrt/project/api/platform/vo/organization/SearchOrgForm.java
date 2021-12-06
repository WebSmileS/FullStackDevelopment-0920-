package com.yrt.project.api.platform.vo.organization;

import java.io.Serializable;

import com.yrt.common.utils.sql.SqlUtil;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 前端机构搜索内容
 * @author Runner
 *
 */
@Data
@ApiModel(value="前端机构搜索内容", description="前端机构搜索内容")
public class SearchOrgForm implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/** 统一社会信用代码 */
	@ApiModelProperty(value="统一社会信用代码",required=false)
	private String social_credit_code;

	/** 名称* */
	@ApiModelProperty(value="名称",required=false)
	private String name = "";
	
	/** 注册人(联系人) */
	@ApiModelProperty(value="注册人",required=false)
	private String registrant;
	
	/** 行政区划代码 */
	@ApiModelProperty(value="行政区划代码",required=false)
	private Long administrative_division_sn;
	
	public SearchOrgForm escapeLikeValue() {
		name = SqlUtil.escapeLikeValue(name);
		social_credit_code = SqlUtil.escapeLikeValue(social_credit_code);
		registrant = SqlUtil.escapeLikeValue(registrant);
		return this;
	}
}
