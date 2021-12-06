package com.yrt.project.api.business.warehouse.vo.central;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.yrt.common.utils.sql.SqlUtil;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "前端搜索产品信息", description = "前端搜索产品信息")
public class SearchNoticeProductForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "经销商(租户)ID", required = true)
	@NotNull(message = "经销商(租户)ID不能为空")
	private Long dealer_inner_sn;

	@ApiModelProperty(value = "产品名称", required = false)
	private String product_name;

	@ApiModelProperty(value = "厂商ID", required = false)
	private Long prodict_vendor_inner_sn;

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

	public Long getDealer_inner_sn() {
		return dealer_inner_sn;
	}

	public void setDealer_inner_sn(Long dealer_inner_sn) {
		this.dealer_inner_sn = dealer_inner_sn;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public Long getProdict_vendor_inner_sn() {
		return prodict_vendor_inner_sn;
	}

	public void setProdict_vendor_inner_sn(Long prodict_vendor_inner_sn) {
		this.prodict_vendor_inner_sn = prodict_vendor_inner_sn;
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

	public SearchNoticeProductForm escapeLikeValue() {
		product_name = SqlUtil.escapeLikeValue(product_name);
		specification_vendor_sn = SqlUtil.escapeLikeValue(specification_vendor_sn);
		specification = SqlUtil.escapeLikeValue(specification);
		return this;
	}
}
