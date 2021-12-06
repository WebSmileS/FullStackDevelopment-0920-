package com.yrt.project.modular.product.domain;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class Product extends DealerProduct implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 厂商内部编号 */
	@ApiModelProperty(value = "厂商内部编号 ")
	private Long vendor_inner_sn;
	/** 厂商编号 */
	@ApiModelProperty(value = "厂商名称")
	private String vendor_name;
	/** 发布员工内部编号 */
	@ApiModelProperty(value = "发布员工内部编号")
	private Long release_employee_inner_sn;
	/** 数据来源 */
	@ApiModelProperty(value = "数据来源 ")
	private Integer data_source;
	/** 发布状态 */
	@ApiModelProperty(value = "发布状态")
	private Integer release_status;
	/** 产品类型 0-医疗器械 1-非医疗器械 */
	@ApiModelProperty(value = "产品类型 0-医疗器械 1-非医疗器械")
	private Integer type;
	/** 医疗器械分类目录类别:1-一类 2-二类 3-三类 */
	@ApiModelProperty(value = "医疗器械分类目录类别:1-一类 2-二类 3-三类 ")
	private Integer code68_type;
	/** 产品编号 */
	@ApiModelProperty(value = "厂商产品编号")
	private String product_vendor_sn;
	/** 注册证号 */
	@ApiModelProperty(value = "注册证号")
	private String cert_no;
	@ApiModelProperty(value = "常用包装单位内部编号")
	private String product_uniform_sn;
	
}
