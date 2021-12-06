package com.yrt.project.api.common.vo;

import javax.validation.constraints.NotNull;

import com.yrt.common.dict.BillsType;
import com.yrt.project.api.platform.vo.product.SearchProductForm;

import io.swagger.annotations.ApiModelProperty;

public class CommSearchProductForm extends SearchProductForm {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "相关机构ID", required = true)
	@NotNull(message = "相关机构ID不能为空")
	private Long organization_inner_sn;
	
	@ApiModelProperty(value="单据类型",required=true)
	@NotNull(message = "单据类型不能为空")
	private BillsType billsType;
	
	@ApiModelProperty(value="仅显示合同内产品,默认true",required=false)
	private Boolean onlyContrctProduct = true;
	
	@ApiModelProperty(value="是否采购,默认true",required=false)
	private Boolean isPurchase = true;

	public BillsType getBillsType() {
		return billsType;
	}

	public void setBillsType(BillsType billsType) {
		this.billsType = billsType;
	}

	public Long getOrganization_inner_sn() {
		return organization_inner_sn;
	}

	public void setOrganization_inner_sn(Long organization_inner_sn) {
		this.organization_inner_sn = organization_inner_sn;
	}

	public Boolean getOnlyContrctProduct() {
		return onlyContrctProduct;
	}

	public void setOnlyContrctProduct(Boolean onlyContrctProduct) {
		this.onlyContrctProduct = onlyContrctProduct;
	}

	public Boolean getIsPurchase() {
		return isPurchase;
	}

	public void setIsPurchase(Boolean isPurchase) {
		this.isPurchase = isPurchase;
	}
	
}
