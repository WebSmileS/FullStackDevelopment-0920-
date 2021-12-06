package com.yrt.project.api.business.dealer.vo.dealerproduct;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.yrt.common.utils.PinyinUtils;
import com.yrt.common.utils.StringUtils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="前端编辑产品信息",description="前端编辑产品信息")
public class EditDealerProductForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 自定义分类内部编号 */
	@ApiModelProperty(value="自定义分类内部编号",required=false)
	private Long type_inner_sn;
	private Long type_inner_sn_old;
	/** 医疗器械分类目录编号 */
	@ApiModelProperty(value="医疗器械68分类编号",required=false)
	private Long code68_sn;
	private Long code68_sn_old;
	/** 存储条件 */
	@ApiModelProperty(value="存储条件 0-其他 1-常温 2-保温 3-阴凉 4-冷藏 5-冷冻 6-无菌 7- 特储",required=false)
	private Integer storage_condition;
	private Integer storage_condition_old;
	/** 医保类型 */
	@ApiModelProperty(value="医保类型 0-A型 1-B型 2-C型",required=false)
	private Integer health_care_type;
	private Integer health_care_type_old;
	/** 名称* */
	@ApiModelProperty(value="名称",required=false)
	private String name;
	private String name_old;
	/** 型号规格(总览) */
	@ApiModelProperty(value="型号规格(总览)",required=false)
	private String specifications;
	private String specifications_old;
	/** 描述 */
	@ApiModelProperty(value="描述",required=false)
	private String description;
	private String description_old;
	
	@ApiModelProperty(value="状态",required=false)
	private Integer status;
	private Integer status_old;
	
	@ApiModelProperty(hidden = true)
	private Map<String,Object> info = new HashMap<String,Object>();
	
	@ApiModelProperty(hidden = true)
	public boolean hasUpdate() {
		if(StringUtils.isNotNull(type_inner_sn) || StringUtils.isNotNull(type_inner_sn_old)) {
			if(StringUtils.isNull(type_inner_sn)) {
				type_inner_sn = -1L;
			}
			info.put("type_inner_sn", type_inner_sn);
		}
		if(StringUtils.isNotNull(code68_sn) || StringUtils.isNotNull(code68_sn_old)) {
			if(StringUtils.isNull(code68_sn)) {
				code68_sn = -1L;
			}
			info.put("code68_sn", code68_sn);
		}
		if(StringUtils.isNotNull(health_care_type) || StringUtils.isNotNull(health_care_type_old)) {
			info.put("health_care_type", health_care_type);
		}
		if(StringUtils.isNotNull(storage_condition) || StringUtils.isNotNull(storage_condition_old)) {
			info.put("storage_condition", storage_condition);
		}
		if(StringUtils.isNotEmpty(name) || StringUtils.isNotEmpty(name_old)) {
			info.put("name", name.trim());
			info.put("pinyin", PinyinUtils.getPy(name.trim()));
		}
		if(StringUtils.isNotEmpty(specifications) || StringUtils.isNotEmpty(specifications_old)) {
			info.put("specifications", specifications);
		}
		if(StringUtils.isNotEmpty(description) || StringUtils.isNotEmpty(description_old)) {
			info.put("description", description);
		}
		if(StringUtils.isNotNull(status) || StringUtils.isNotNull(status_old)) {
			info.put("status", status);
		}
		return StringUtils.isNotEmpty(info);
	}
	
	@ApiModelProperty(hidden = true)
	public Map<String,Object> getInfo(){
		return info;
	}
}
