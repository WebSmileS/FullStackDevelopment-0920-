package com.yrt.project.api.common.vo;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="前端注册机构内容", description="前端注册机构内容")
public class AddNoAuditOrgForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public interface Update{};
	public interface Add{};
	
	/** 行政区划ID */
	@ApiModelProperty(value="行政区划ID",required=true)
	@NotNull(message="行政区划不能为空")
	private Long administrative_division_sn;
	/** 系统类型 */
	@ApiModelProperty(value="系统类型 1-医院 、 2-经销商、 3-厂商",required=true)
	@NotNull(message="系统类型不能为空")
	private Integer system_type;
	/** 名称 */
	@ApiModelProperty(value="名称",required=true)
	@NotBlank(message="机构注册名称不能为空")
	@Length(max=200,message="机构名称不能超过200个字符")
	private String name;
	/** 别名 */
	@ApiModelProperty(value="别名",required=false)
	@Length(max=200,message="机构别名不能超过200个字符")
	private String alias = "";
	/** 地址 */
	@ApiModelProperty(value="地址",required=false)
	@Length(max=100,message="地址不能超过100个字符")
	private String address = "";
	/** 注册人 */
	@ApiModelProperty(value="注册人",required=false)
	@Length(max=18,message="注册人不能超过18个字符")
	private String registrant = "";
	/** 注册人电话 */
	@ApiModelProperty(value="注册人电话",required=true)
	@NotBlank(message="注册电话不能为空")
//	@Pattern(regexp = "^0?(13[0-9]|15[012356789]|17[0123456789]|18[0123456789]|19[0123456789]|14[57])[0-9]{8}$",message="电话格式错误")
	private String registrant_phone;
	/** 注册人电子邮件 */
	@Email(message="电子邮件格式错误")
	@ApiModelProperty(value="注册人电子邮件",required=false)
	private String registrant_email = "";
	/** 描述 */
	@ApiModelProperty(value="描述",required=false)
	@Length(max=300,message="描述不能超过300个字符")
	private String description = "";
	/**统一社会信用代码*/
	@ApiModelProperty(value="统一社会信用代码",required=false)
//	@NotBlank(message="统一社会信用代码不能为空")
	@Length(max=18,message="统一社会信用代码不能超过18个字符")
	private String social_credit_code;

}
