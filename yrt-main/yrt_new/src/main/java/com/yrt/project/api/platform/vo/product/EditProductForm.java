package com.yrt.project.api.platform.vo.product;

import java.io.Serializable;

import com.yrt.common.utils.StringUtils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="前端编辑产品信息",description="前端编辑产品信息")
public class EditProductForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 厂商内部编号 */
//	@ApiModelProperty(value="厂商ID",required=false)
//	private Long vendor_inner_sn;
//	private Long vendor_inner_sn_old;
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
	/** 产品类型 0-医疗器械 1-非医疗器械 */
	@ApiModelProperty(value="产品类型 0-医疗器械 1-非医疗器械",required=false)
	private Integer type;
	private Integer type_old;
	/** 名称* */
	@ApiModelProperty(value="名称",required=false)
	private String name;
	private String name_old;
	/** 型号规格(总览) */
	@ApiModelProperty(value="型号规格(总览)",required=false)
	private String specifications;
	private String specifications_old;
	/** 医保编号 */
	/** 描述 */
	@ApiModelProperty(value="描述",required=false)
	private String description;
	private String description_old;
	
	@ApiModelProperty(value="状态",required=false)
	private Integer status;
	private Integer status_old;
	
	/** 医疗器械分类目录类别:1-一类 2-二类 3-三类 */
	@ApiModelProperty(value="医疗器械分类目录类别:1-一类 2-二类 3-三类",required=false)
	private Integer code68_type;
	private Integer code68_type_old;
	
	public boolean hasUpdate() {
		boolean hasUpdate = false;
		if(StringUtils.isNotNull(type_inner_sn) || StringUtils.isNotNull(type_inner_sn_old)) {
			if(StringUtils.isNull(type_inner_sn) && StringUtils.isNotNull(type_inner_sn_old)) {
				type_inner_sn = -1L;
			}
			hasUpdate = true;
		}
		if(StringUtils.isNotNull(code68_sn) || StringUtils.isNotNull(code68_sn_old)) {
			if(StringUtils.isNull(code68_sn) && StringUtils.isNotNull(code68_sn_old)) {
				code68_sn = -1L;
			}
			hasUpdate = true;
		}
		if(StringUtils.isNotNull(health_care_type) || StringUtils.isNotNull(storage_condition) 
				|| StringUtils.isNotEmpty(name)  || StringUtils.isNotNull(type)
				|| (StringUtils.isNotEmpty(specifications) || StringUtils.isNotEmpty(specifications_old))
				|| (StringUtils.isNotEmpty(description) || StringUtils.isNotEmpty(description_old))
				|| (StringUtils.isNotNull(status) || StringUtils.isNotNull(status_old))
				|| (StringUtils.isNotNull(code68_type) || StringUtils.isNotNull(code68_type_old))){
			hasUpdate = true;
		}
		if(StringUtils.isNotEmpty(name)) {
			name = name.trim();
		}
		return hasUpdate;
	}
}
