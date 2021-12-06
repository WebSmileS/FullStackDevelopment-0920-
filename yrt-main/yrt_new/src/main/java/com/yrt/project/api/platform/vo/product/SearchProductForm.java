package com.yrt.project.api.platform.vo.product;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.yrt.common.utils.sql.SqlUtil;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "前端搜索产品信息", description = "前端搜索产品信息")
public class SearchProductForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "当前机构ID", required = true)
	@NotNull(message = "当前机构ID不能为空")
	private Long org_id;

	/** 名称* */
	@ApiModelProperty(value = "产品名称", required = false)
	private String name;

	/** 厂商ID */
	@ApiModelProperty(value = "厂商ID", required = false)
	private Long vendor_inner_sn;

	/** 厂商ID */
	@ApiModelProperty(value = "如果相关机构是经销商,必须填写这个参数为相关机构ID", required = false)
	private Long dealer_inner_sn;

	/** 自定义类型编号 */
	@ApiModelProperty(value = "自定义类型编号", required = false)
	private Long type_inner_sn;

	/** 68分类编号 */
	@ApiModelProperty(value = "68分类编号", required = false)
	private Long code68_sn;

	/** 型号规格厂商自定编号 */
	@ApiModelProperty(value = "型号规格厂商自定编号", required = false)
	private String specification_vendor_sn;

	/** 型号规格 */
	@ApiModelProperty(value = "型号规格", required = false)
	private String specification;
	
	@ApiModelProperty(value = "注册证号", required = false)
	private String register_sn;

	@ApiModelProperty(value = "搜索关键字", required = false)
	private String key;

	public String getRegister_sn() {
		return register_sn;
	}

	public void setRegister_sn(String register_sn) {
		this.register_sn = register_sn;
	}

	public Long getOrg_id() {
		return org_id;
	}

	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getVendor_inner_sn() {
		return vendor_inner_sn;
	}

	public void setVendor_inner_sn(Long vendor_inner_sn) {
		this.vendor_inner_sn = vendor_inner_sn;
	}

	public Long getDealer_inner_sn() {
		return dealer_inner_sn;
	}

	public void setDealer_inner_sn(Long dealer_inner_sn) {
		this.dealer_inner_sn = dealer_inner_sn;
	}

	public Long getType_inner_sn() {
		return type_inner_sn;
	}

	public void setType_inner_sn(Long type_inner_sn) {
		this.type_inner_sn = type_inner_sn;
	}

	public Long getCode68_sn() {
		return code68_sn;
	}

	public void setCode68_sn(Long code68_sn) {
		this.code68_sn = code68_sn;
	}

	public String getSpecification_vendor_sn() {
		return specification_vendor_sn;
	}

	public void setSpecification_vendor_sn(String specification_vendor_sn) {
		this.specification_vendor_sn = specification_vendor_sn;
	}

	public String getSpecification() {
		return specification;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public SearchProductForm escapeLikeValue() {
		key = SqlUtil.escapeLikeValue(key);
		name = SqlUtil.escapeLikeValue(name);
		specification_vendor_sn = SqlUtil.escapeLikeValue(specification_vendor_sn);
		specification = SqlUtil.escapeLikeValue(specification);
		return this;
	}
}
