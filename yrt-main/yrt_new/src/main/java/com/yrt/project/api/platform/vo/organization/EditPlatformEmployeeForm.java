package com.yrt.project.api.platform.vo.organization;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.utils.StringUtils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@Data
public class EditPlatformEmployeeForm implements Serializable {
	private static final long serialVersionUID = 1L;	
	
	/** 员工内部编号 */
	@ApiModelProperty(value="员工内部编号",required=true)
	private Long employee_inner_sn;
	
	/** 上级员工内部编号 */
	@ApiModelProperty(value="上级员工内部编号",required=false)
	private Long parent_employee_inner_sn;
	private Long parent_employee_inner_sn_old;
	/** 性别 */
	@ApiModelProperty(value="性别",required=false)
	private Integer sex;
	private Integer sex_old;
	/** 状态 */
	@ApiModelProperty(value="状态",required=false)
	private Integer status;
	private Integer status_old;
	/** 个人证件类别 */
	@ApiModelProperty(value="个人证件类别",required=false)
	private Integer personal_certificate_type;
	private Integer personal_certificate_type_old;
	/** 学历 */
	@ApiModelProperty(value="学历",required=false)
	private Integer degree;
	private Integer degree_old;
	/** 婚姻状况 */
	@ApiModelProperty(value="婚姻状况",required=false)
	private Integer marriage_status;
	private Integer marriage_status_old;
	/** 生日 */
	@ApiModelProperty(value="生日",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date birthday;
	private Date birthday_old;
	/** 入职日期 */
	@ApiModelProperty(value="入职日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date entry_date;
	private Date entry_date_old;
	/** 离职日期 */
	@ApiModelProperty(value="离职日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date departure_date;
	private Date departure_date_old;
	/** 姓名* */
	@ApiModelProperty(value="名称",required=false)
	@NotBlank(message="名称不能为空")
	@Length(max=208,message="部门不能超过50个字符")
	private String name;
	private String name_old;
	/** 员工编号 */
	@ApiModelProperty(value="员工编号",required=false)
	private String employee_sn;
	private String employee_sn_old;
	/** 民族 */
	@ApiModelProperty(value="民族",required=false)
	private String nation;
	private String nation_old;
	/** 职位 */
	@ApiModelProperty(value="职位",required=false)
	private String position;
	private String position_old;
	/** 个人证件号 */
	@ApiModelProperty(value="个人证件号",required=false)
	private String personal_certificate_sn;
	private String personal_certificate_sn_old;
	/** 电话 */
	@ApiModelProperty(value="电话",required=false)
	private String phone;
	private String phone_old;
	/** 电子邮件 */
	@ApiModelProperty(value="电子邮件",required=false)
	private String email;
	private String email_old;
	/** QQ */
	@ApiModelProperty(value="QQ",required=false)
	private String qq;
	private String qq_old;
	/** 描述 */
	@ApiModelProperty(value="描述",required=false)
	private String description;
	private String description_old;
	/** 照片URL */
	@ApiModelProperty(value="照片URL",required=false)
	private String photo_url;
	private String photo_url_old;
	/** 所属部门id列表 */
	@ApiModelProperty(value="部门id列表(用逗号分隔)",required=false)
	private String department_inner_sn;
	private String department_inner_sn_old;
	/** 能否登录状态 */
	@ApiModelProperty(value="是否登录系统",required=false)
	private Integer login_account_status;
	private Integer login_account_status_old ;
	/** 系统登录id */
	@ApiModelProperty(value="系统登录内部id",required=false)
	private Long user_inner_sn;
	private Long user_inner_sn_old ;
	/** 角色内部编号 */
//	@ApiModelProperty(value="员工内部编号",required=false)
//	private Long role_inner_sn;
//	private Long role_inner_sn_old;
	
	@ApiModelProperty(value="员工内部编号",hidden = true)
	private Map<String, Object> employees = new HashMap<String, Object>();
	@ApiModelProperty(value="员工内部编号",hidden = true)
	public boolean isUpdate() {
		if(StringUtils.isNotNull(parent_employee_inner_sn) || StringUtils.isNotNull(parent_employee_inner_sn_old)) {
			if(StringUtils.isNull(parent_employee_inner_sn)) {
				employees.put("parent_employee_inner_sn_null",1);
			}
			employees.put("parent_employee_inner_sn", parent_employee_inner_sn);
		}
		if(StringUtils.isNotNull(sex) || StringUtils.isNotNull(sex_old)) {
			employees.put("sex", sex);
		}
		if(StringUtils.isNotNull(status) || StringUtils.isNotNull(status_old)) {
			employees.put("status", status);
		}
		if(StringUtils.isNotNull(personal_certificate_type) || StringUtils.isNotNull(personal_certificate_type_old)) {
			employees.put("personal_certificate_type", personal_certificate_type);
		}
		if(StringUtils.isNotNull(degree) || StringUtils.isNotNull(degree_old)) {
			employees.put("degree", degree);
		}
		if(StringUtils.isNotNull(marriage_status) || StringUtils.isNotNull(marriage_status_old)) {
			employees.put("marriage_status", marriage_status);
		}
		if(StringUtils.isNotNull(birthday) || StringUtils.isNotNull(birthday_old)) {
			if(StringUtils.isNull(birthday)) {
				employees.put("birthday_null", 1);
			}
			employees.put("birthday",birthday);
		}
		if(StringUtils.isNotNull(entry_date) || StringUtils.isNotNull(entry_date_old)) {
			if(StringUtils.isNull(entry_date)) {
				employees.put("entry_date_null", 1);
			}
			employees.put("entry_date", entry_date);
		}
		if(StringUtils.isNotNull(departure_date) || StringUtils.isNotNull(departure_date_old)) {
			if(StringUtils.isNull(departure_date)) {
				employees.put("departure_date_null", 1);
			}
			employees.put("departure_date", departure_date);
		}
		if(StringUtils.isNotEmpty(name) || StringUtils.isNotEmpty(name_old)) {
			employees.put("name", name);
		}
		if(StringUtils.isNotEmpty(employee_sn) || StringUtils.isNotEmpty(employee_sn_old)) {
			employees.put("employee_sn", employee_sn);
		}
		if(StringUtils.isNotEmpty(nation) || StringUtils.isNotEmpty(nation_old)) {
			employees.put("nation", nation);
		}
		if(StringUtils.isNotEmpty(position) || StringUtils.isNotEmpty(position_old)) {
			employees.put("position", position);
		}
		if(StringUtils.isNotEmpty(personal_certificate_sn) || StringUtils.isNotEmpty(personal_certificate_sn_old)) {
			employees.put("personal_certificate_sn", personal_certificate_sn);
		}
		if(StringUtils.isNotEmpty(phone) || StringUtils.isNotEmpty(phone_old)) {
			employees.put("phone", phone);
		}
		if(StringUtils.isNotEmpty(email) || StringUtils.isNotEmpty(email_old)) {
			employees.put("email", email);
		}
		if(StringUtils.isNotEmpty(qq) || StringUtils.isNotEmpty(qq_old)) {
			employees.put("qq", qq);
		}
		if(StringUtils.isNotEmpty(description) || StringUtils.isNotEmpty(description_old)) {
			employees.put("description", description);
		}
		if(StringUtils.isNotEmpty(photo_url) || StringUtils.isNotEmpty(photo_url_old)) {
			employees.put("photo_url", photo_url);
		}
		if(StringUtils.isNotNull(login_account_status) || StringUtils.isNotNull(login_account_status_old)) {
			employees.put("login_account_status", login_account_status);
		}
		return StringUtils.isNotEmpty(employees);
	}
	
	public boolean isUpdateDept() {
		if(StringUtils.isNotEmpty(department_inner_sn) || StringUtils.isNotEmpty(department_inner_sn_old)) {
			return true;
		}
		return false;
	}

	/*
	 * public boolean isUpdateRole() { if(StringUtils.isNotNull(role_inner_sn) ||
	 * StringUtils.isNotNull(role_inner_sn_old)) { return true; } return false; }
	 */
	@ApiModelProperty(value="员工内部编号",hidden = true)
	public Map<String, Object> getEmployee() {
		return employees;
	}
}
