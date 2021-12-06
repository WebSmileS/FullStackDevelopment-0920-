package com.yrt.project.modular.organization.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 经销商第二类医疗器械经营备案凭证
 * @author Runner
 *
 */
@Data
public class DealerSTMDBRC implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 经销商内部编号 */
	private Long id;
	
	/** 经营方式 */
	private Integer operation_mode;
	
	/** 备案日期 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date issue_date;
	
	/** 备案编号 */
	private String record_sn;
	
	/** 经营场所 */
	private String premises;
	
	/** 库房地址 */
	private String warehouse_address;
	
	/** 备案部门 */
	private String issue_organization;
	
	/** 经营范围 */
	private String business_scope;
	
	private String business_scope_name;

}
