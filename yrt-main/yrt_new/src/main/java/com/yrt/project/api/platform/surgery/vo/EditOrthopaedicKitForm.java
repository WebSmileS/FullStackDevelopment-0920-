package com.yrt.project.api.platform.surgery.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.StringUtils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EditOrthopaedicKitForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 工具包内部编号 */
	@ApiModelProperty(value="工具包ID",required=true)
	@NotNull(message = "工具包ID不能为空")
	private Long kit_inner_sn;
	/** 类别内部编号 */
	@ApiModelProperty(value="类别ID",required=false)
	private Long type_inner_sn;
	private Long type_inner_sn_old;
	/** 品牌内部编号 */
	@ApiModelProperty(value="品牌内部编号",required=false)
	private Long brand_inner_sn;
	private Long brand_inner_sn_old;
	/** 名称 */
	@ApiModelProperty(value="名称",required=false)
	private String name;
	private String name_old;
	/** 描述 */
	@ApiModelProperty(value="描述",required=false)
	private String description;
	private String description_old;
	/** 工具包编号 */
	@ApiModelProperty(value="工具包编号",required=false)
	private String kit_sn;
	private String kit_sn_old;
	
	@ApiModelProperty(value="工具包明细",required=false)
	private List<OrthopaedicDetailForm> details = new ArrayList<>();
	
	@ApiModelProperty(hidden = true)
	private Map<String,Object> info = new HashMap<>();
	
	@ApiModelProperty(hidden = true)
	public Map<String,Object> getInfo(){
		return info;
	}
	
	public boolean hasUpdate() {
		if(StringUtils.isNotNull(type_inner_sn) || StringUtils.isNotNull(type_inner_sn_old)) {
			if(StringUtils.isNull(type_inner_sn)) {
				throw new ValidateException(MessageUtils.message("param.not.null", "手术类别"));
			}
			info.put("type_inner_sn", type_inner_sn);
		}
		
		if(StringUtils.isNotNull(brand_inner_sn) || StringUtils.isNotNull(brand_inner_sn)) {
			if(StringUtils.isNull(brand_inner_sn)) {
				throw new ValidateException(MessageUtils.message("param.not.null", "品牌类别"));
			}
			info.put("brand_inner_sn", brand_inner_sn);
		}
		
		if(StringUtils.isNotEmpty(name) || StringUtils.isNotEmpty(name_old)) {
			info.put("name", name);
		}
		
		if(StringUtils.isNotEmpty(description) || StringUtils.isNotEmpty(description_old)) {
			info.put("description", description);
		}
		if(StringUtils.isNotEmpty(kit_sn) || StringUtils.isNotEmpty(kit_sn_old)) {
			info.put("kit_sn", kit_sn);
		}
		
		
		return StringUtils.isNotEmpty(info);
	}
}
