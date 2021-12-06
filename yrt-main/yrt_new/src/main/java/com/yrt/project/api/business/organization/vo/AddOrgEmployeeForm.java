package com.yrt.project.api.business.organization.vo;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddOrgEmployeeForm implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 所属部门 */
	@ApiModelProperty(value="所属部门，多个部门用英文逗号分隔",required=false)
	private String department_inner_sn = "";
	/** 上级员工内部编号 */
	@ApiModelProperty(value="上级员工内部编号",required=false)
	private Long parent_employee_inner_sn;
	/** 性别 */
	@ApiModelProperty(value="性别",required=true)
	private Integer sex;
	/** 状态 */
	@ApiModelProperty(value="状态",required=true)
	private Integer status;
	/** 个人证件类别 */
	@ApiModelProperty(value="个人证件类别",required=true)
	private Integer personal_certificate_type;
	/** 学历 */
	@ApiModelProperty(value="学历",required=true)
	private Integer degree;
	/** 婚姻状况 */
	@ApiModelProperty(value="婚姻状况",required=true)
	private Integer marriage_status;
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
	@Length(max=50,message="名称不能超过18个字符")
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
//	@ApiModelProperty(value="是否创建登录账户",required=false)
//	private boolean createLoginAccount = false;
	/** 能否登录状态 */
	@ApiModelProperty(value="是否登录系统",required=false)
	private Integer login_account_status;
	/** 角色内部编号 */
	@ApiModelProperty(value="员工内部编号",required=false)
	private Long role_inner_sn;
}
