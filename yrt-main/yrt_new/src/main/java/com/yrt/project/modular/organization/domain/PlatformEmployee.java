package com.yrt.project.modular.organization.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PlatformEmployee  implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "员工内部编号")
	private Long employee_inner_sn;
	@ApiModelProperty(value = "上级员工内部编号")
	private Long parent_employee_inner_sn;
	@ApiModelProperty(value = "性别")
	private Integer sex;
	@ApiModelProperty(value = "状态")
	private Integer status;
	@ApiModelProperty(value = "个人证件类别")
	private Integer personal_certificate_type;
	@ApiModelProperty(value = "学历")
	private Integer degree;
	@ApiModelProperty(value = "婚姻状况")
	private Integer marriage_status;
	@ApiModelProperty(value = "生日")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date birthday;
	@ApiModelProperty(value = "入职日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date entry_date;
	@ApiModelProperty(value = "离职日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date departure_date;
	@ApiModelProperty(value = "姓名")
	private String name;
	@ApiModelProperty(value = "员工编号")
	private String employee_sn;
	@ApiModelProperty(value = "民族")
	private String nation;
	@ApiModelProperty(value = "职位")
	private String position;
	@ApiModelProperty(value = "个人证件号")
	private String personal_certificate_sn;
	@ApiModelProperty(value = "电话")
	private String phone;
	@ApiModelProperty(value = "电子邮件")
	private String email;
	@ApiModelProperty(value = "QQ号码")
	private String qq;
	@ApiModelProperty(value = "描述")
	private String description;
	@ApiModelProperty(value = "照片URL")
	private String photo_url;
	@ApiModelProperty(value = "上帝内部编号")
	private Long user_inner_sn;
	@ApiModelProperty(value = "登录账号状态")
	private Integer login_account_status;
	
	@ApiModelProperty(value = "部门内部编号")
	private String department_inner_sn = "";
	@ApiModelProperty(value = "部门名称")
	private String department_name = "";
	@ApiModelProperty(value = "下级员工列表")
	private List<PlatformEmployee> children = new ArrayList<>();
	
	@ApiModelProperty(value = "角色内部编号")
	private Long role_inner_sn;

}
