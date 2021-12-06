package com.yrt.project.api.platform.vo.organization;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddPlatformEmployeeForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 员工内部编号 */
	@ApiModelProperty(value="员工内部编号",required=false,hidden=true)
	private Long employee_inner_sn;

	/** 上级员工内部编号 */
	@ApiModelProperty(value="上级员工内部编号",required=false)
	private Long parent_employee_inner_sn;
	/** 性别 */
	@ApiModelProperty(value="性别",required=true)
	private int sex;
	/** 状态 */
	@ApiModelProperty(value="状态",required=true)
	private int status;
	/** 个人证件类别 */
	@ApiModelProperty(value="个人证件类别",required=true)
	private int personal_certificate_type;
	/** 学历 */
	@ApiModelProperty(value="学历",required=true)
	private int degree;
	/** 婚姻状况 */
	@ApiModelProperty(value="婚姻状况",required=true)
	private int marriage_status;
	/** 生日 */
	@ApiModelProperty(value="生日",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date birthday;
	/** 入职日期 */
	@ApiModelProperty(value="入职日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date entry_date;
	/** 离职日期 */
	@ApiModelProperty(value="离职日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date departure_date;
	/** 姓名* */
	@ApiModelProperty(value="名称",required=true)
	@NotBlank(message="名称不能为空")
	@Length(max=208,message="部门不能超过50个字符")
	private String name;
	/** 员工编号 */
	@ApiModelProperty(value="员工编号",required=false)
	private String employee_sn = "";
	/** 民族 */
	@ApiModelProperty(value="民族",required=false)
	private String nation = "";
	/** 职位 */
	@ApiModelProperty(value="职位",required=false)
	private String position = "";
	/** 个人证件号 */
	@ApiModelProperty(value="个人证件号",required=false)
	private String personal_certificate_sn = "";
	/** 电话 */
	@ApiModelProperty(value="电话",required=false)
	private String phone = "";
	/** 电子邮件 */
	@ApiModelProperty(value="电子邮件",required=false)
	private String email = "";
	/** QQ */
	@ApiModelProperty(value="QQ",required=false)
	private String qq = "";
	/** 描述 */
	@ApiModelProperty(value="描述",required=false)
	private String description = "";
	/** 照片URL */
	@ApiModelProperty(value="照片URL",required=false)
	private String photo_url = "";
	@ApiModelProperty(value="是否创建登录账户",required=false)
	private int login_account_status = 0;
	/** 所属部门id列表 */
	@ApiModelProperty(value="部门id列表(用逗号分隔)",required=false)
	private String department_inner_sn = "";
	/** 角色内部编号 */
	@ApiModelProperty(value="员工内部编号",required=false)
	private Long role_inner_sn;
}
