package com.yrt.project.modular.organization.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 医院医疗机构执业许可证
 * @author Runner
 *
 */
@Data
public class HospitalPLHMI implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 医院内部编号 */
	private Long id;
	
	/** 开始日期 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date begin_date;
	
	/** 截止日期 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date end_date;
	
	/** 颁发日期 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date issue_date;
	
	/** 登记号 */
	private String registered_number;
	
	/** 发证机关 */
	private String issue_organization;
	
	/** 诊疗科目 */
	private String diagnosis_subject;
	
}
