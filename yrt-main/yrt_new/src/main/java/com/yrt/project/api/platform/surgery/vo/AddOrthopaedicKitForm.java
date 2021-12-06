package com.yrt.project.api.platform.surgery.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddOrthopaedicKitForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 名称 */
	@ApiModelProperty(value="名称",required=true)
	@NotBlank(message = "名称不能为空")
	private String name;
	/** 手术包编号 */
	@ApiModelProperty(value="工具包编号",required=false)
	private String kit_sn = "";
	/** 描述 */
	@ApiModelProperty(value="产品描述",required=false)
	private String description = "";
	/** 类别内部编号 */
	@ApiModelProperty(value="类别ID",required=true)
	@NotNull(message = "类别ID不能为空")
	private Long type_inner_sn;
	/** 品牌内部编号 */
	@ApiModelProperty(value="品牌内部编号",required=true)
	@NotNull(message = "品牌内部编号")
	private Long brand_inner_sn;
	
	@ApiModelProperty(value="关键字",required=false)
	private String keywords;
	
	@ApiModelProperty(value="工具包明细",required=false)
	private List<OrthopaedicDetailForm> details = new ArrayList<>();
}
