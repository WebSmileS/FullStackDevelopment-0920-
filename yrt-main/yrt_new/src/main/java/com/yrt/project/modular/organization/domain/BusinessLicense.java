package com.yrt.project.modular.organization.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 机构营业执照信息
 * @author Runner
 *
 */
@Data
public class BusinessLicense implements Serializable {

	private static final long serialVersionUID = 1L;

	/**  */
	private Long id;
	
	/** 开始日期 */
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date begin_date;
	
	/** 截止日期 */
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date;
	
	/** 颁发日期 */
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date issue_date;
	
	/** 登记机关 */
	private String issue_organization;
	
	/** 经营范围 */
	private String business_scope;

}
