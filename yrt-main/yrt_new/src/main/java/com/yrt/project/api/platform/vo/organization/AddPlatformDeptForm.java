package com.yrt.project.api.platform.vo.organization;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
 
@Data
public class AddPlatformDeptForm  implements Serializable{
private static final long serialVersionUID = 1L;
	
	/** 部门内部编号 */
	@ApiModelProperty(value="部门内部编号",required=false, hidden=true)
	private Long department_inner_sn;
	/** 上级部门内部编号 */
	@ApiModelProperty(value="上级部门内部编号",required=false)
	private Long parent_department_inner_sn;
	/** 主管员工内部编号 */
	@ApiModelProperty(value="主管员工内部编号",required=false)
	private Long director_inner_sn;
	/** 名称* */
	@ApiModelProperty(value="部门名称",required=true)
	@NotBlank(message="部门名称不能为空")
	@Length(max=208,message="部门名称不能超过50个字符")
	private String name;
	/** 部门编号 */
	@ApiModelProperty(value="部门编号",required=false)
	private String department_sn = "";
	/** 电话 */
	@ApiModelProperty(value="电话",required=false)
	private String phone = "";
	/** 描述 */
	@ApiModelProperty(value="描述",required=false)
	private String description = "";

}
