package com.yrt.project.api.platform.vo.organization;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class NoAuditOrgForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 行政区划ID */
	private Long administrative_division_sn;
	private Long administrative_division_sn_old;
	/** 系统类型 */
	private Integer system_type;
	private Integer system_type_old;
	/** 注册状态 0-待审核 1-审核失败 */
	private Integer audit_status;
	private Integer audit_status_old;
	/** 注册时间 */
	private Date register_time;
	private Date register_time_old;
	/** 名称 */
	private String name;
	private String name_old;
	/** 别名 */
	private String alias;
	private String alias_old;
	/** 地址 */
	private String address;
	private String address_old;
	/** 注册人 */
	private String registrant;
	private String registrant_old;
	/** 注册人电话 */
	private String registrant_phone;
	private String registrant_phone_old;
	/** 注册人电子邮件 */
	private String registrant_email;
	private String registrant_email_old;
	/** 描述 */
	private String description;
	private String description_old;
	/**统一社会信用代码*/
	private String social_credit_code;
	private String social_credit_code_old;

}
