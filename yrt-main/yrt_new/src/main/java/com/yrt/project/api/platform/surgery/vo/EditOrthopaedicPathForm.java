package com.yrt.project.api.platform.surgery.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.yrt.common.utils.StringUtils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EditOrthopaedicPathForm implements Serializable {

private static final long serialVersionUID = 1L;
	
	/** 手术径路内部编号  */
	@ApiModelProperty(value="手术径路ID",required=true)
	@NotNull(message = "手术径路ID不能为空")
	private Long op_inner_sn;
	
	/** 上级手术径路内部编号  */
	@ApiModelProperty(value="上级手术径路ID",required=false)
	private Long parent_op_inner_sn;
	private Long parent_op_inner_sn_old;
	/** 名称  */
	@ApiModelProperty(value="名称",required=false)
	private String name;
	private String name_old;
	
	@ApiModelProperty(hidden = true)
	private Map<String,Object> info = new HashMap<>();
	
	@ApiModelProperty(hidden = true)
	public Map<String,Object> getInfo(){
		info.put("op_inner_sn", op_inner_sn);
		
		return info;
	}
	
	public boolean hasUpdate() {
		if(StringUtils.isNotNull(parent_op_inner_sn) || StringUtils.isNotNull(parent_op_inner_sn_old)) {
			if(StringUtils.isNull(parent_op_inner_sn)) {
				info.put("parent_op_inner_sn_null", 1);
			}
			info.put("parent_op_inner_sn", parent_op_inner_sn);
		}
		
		if(StringUtils.isNotEmpty(name) || StringUtils.isNotEmpty(name_old)) {
			info.put("name", name);
		}
		
		return StringUtils.isNotEmpty(info);
	}
}
