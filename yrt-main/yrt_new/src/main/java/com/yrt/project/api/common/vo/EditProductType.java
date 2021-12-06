package com.yrt.project.api.common.vo;

import java.io.Serializable;

import com.yrt.common.utils.StringUtils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="前端修改自定义分类", description="前端修改自定义分类")
public class EditProductType implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/** 状态 0-失效 1-有效 */
	@ApiModelProperty(value="状态 0-失效 1-有效",required=false)
	private Integer status;
	private Integer status_old;
	/** 分类 */
	@ApiModelProperty(value="自定义分类名称",required=false)
	private String type;
	private String type_old;
	/** 分类描述 */
	@ApiModelProperty(value="自定义分类描述",required=false)
	private String description;
	private String description_old;
	
	public boolean hasUpdate() {
		boolean hasUpdate = false;
		if(StringUtils.isNotNull(status) || StringUtils.isNotEmpty(type) 
				|| (StringUtils.isNotEmpty(description) || StringUtils.isNotEmpty(description_old))){
			hasUpdate = true;
		}
		return hasUpdate;
	}
}
