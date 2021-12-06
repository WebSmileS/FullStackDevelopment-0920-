package com.yrt.project.modular.organization.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 厂商医疗器械生产许可证
 * @author Runner
 *
 */
@Data
public class VendorMDPL implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 厂商内部编号 */
	private Long id;
	
	/** 截止日期 */
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date;
	
	/** 颁发日期 */
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date issue_date;
	
	/** 许可证编号 */
	private String license_sn;
	
	/** 生产地址 */
	private String production_address;
	
	/** 发证部门 */
	private String issue_organization;
	
	/** 生产范围 */
	private String production_range;
	
	private String production_range_name;

}
