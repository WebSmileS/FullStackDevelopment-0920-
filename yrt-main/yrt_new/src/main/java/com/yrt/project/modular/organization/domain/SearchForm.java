package com.yrt.project.modular.organization.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public class SearchForm implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 统一社会信用代码 */
	private String social_credit_code;

	/** 名称* */
	private String name;
	
	/** 机构认证状态 0-未认证 1-用户申请认证 2-认证通过 3-认证未通过  */
	private String certification_status;

	/** 状态 0-禁用 1-正常 */
	private Integer status = 1;

	/** 开始时间 */
	private String begin_time;

	/** 结束时间 */
	private String end_time;
	
	/** 别名 */
	private String alias = "";
	
	/** 数据来源 */
	private Integer data_source;
	
	/** 行政区划代码 */
	private Long administrative_division_sn;
	
	private Boolean is_certifie;
	
	private Boolean isAll = false;
}
