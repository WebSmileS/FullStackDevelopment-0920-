package com.yrt.project.api.platform.vo.organization;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.yrt.common.utils.StringUtils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
	public class EditPlatformDeptForm implements Serializable {

		private static final long serialVersionUID = 1L;
		
		@ApiModelProperty(value="不需要前端输入",hidden=true)
		private Map<String, Object> depts = new HashMap<String, Object>();
		

		/** 部门内部编号 */
		@ApiModelProperty(value="部门内部编号",required=true)
		private Long department_inner_sn;

		/** 上级部门内部编号 */
		@ApiModelProperty(value="上级部门内部编号",required=false)
		private Long parent_department_inner_sn;
		private Long parent_department_inner_sn_old;
		/** 主管员工内部编号 */
		@ApiModelProperty(value="主管员工内部编号",required=false)
		private Long director_inner_sn;
		private Long director_inner_sn_old;
		/** 状态 */
		@ApiModelProperty(value="状态",required=false)
		private Integer status;
		private Integer status_old;
		/** 名称* */
		@ApiModelProperty(value="部门名称",required=false)
		private String name;
		private String name_old;
		/** 部门编号 */
		@ApiModelProperty(value="部门编号",required=false)
		private String department_sn;
		private String department_sn_old;
		/** 电话 */
		@ApiModelProperty(value="电话",required=false)
		private String phone;
		private String phone_old;
		/** 描述 */
		@ApiModelProperty(value="描述",required=false)
		private String description;
		private String description_old;
		
		public boolean hasUpdate() {
			if(StringUtils.isNotNull(parent_department_inner_sn) || StringUtils.isNotNull(parent_department_inner_sn_old)) {
				depts.put("parent_department_inner_sn", parent_department_inner_sn);
			}
			if(StringUtils.isNotNull(director_inner_sn) || StringUtils.isNotNull(director_inner_sn_old)) {
				depts.put("director_inner_sn", director_inner_sn);
			}
			if(StringUtils.isNotNull(status) || StringUtils.isNotNull(status_old)) {
				depts.put("status", status);
			}
			if(StringUtils.isNotEmpty(name) || StringUtils.isNotEmpty(name_old)) {
				depts.put("name", name);
			}
			if(StringUtils.isNotEmpty(department_sn) || StringUtils.isNotEmpty(department_sn_old)) {
				depts.put("department_sn", department_sn);
			}
			if(StringUtils.isNotEmpty(phone) || StringUtils.isNotEmpty(phone_old)) {
				depts.put("phone", phone);
			}
			if(StringUtils.isNotEmpty(description) || StringUtils.isNotEmpty(description_old)) {
				depts.put("description", description);
			}
			return StringUtils.isNotEmpty(depts);
		}
		
		public Map<String, Object> getDepts(){
			return depts;
		}

	}


