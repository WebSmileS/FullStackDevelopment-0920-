package com.yrt.project.api.vo;

import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.RefVoucherType;

public class Quantities extends BaseQuantities {

	private static final long serialVersionUID = -6725007796293293169L;

	private BillsType billsType;
	
	private RefVoucherType voucher_type_c;
	
	private Long voucher_inner_sn;
	
	private Long voucher_details_inner_sn;
	
	private Long vendorId;
	
	private Long productId;
	
	private Long specificationId;
	
	private Integer productType;
	
	private Long mdrf_inner_sn;
	
	private String mdrf_sn;
	
	private String health_care_sn;
	
	public BillsType getBillsType() {
		return billsType;
	}

	public void setBillsType(BillsType billsType) {
		this.billsType = billsType;
	}

	public RefVoucherType getVoucher_type_c() {
		return voucher_type_c;
	}

	public void setVoucher_type_c(RefVoucherType voucher_type_c) {
		this.voucher_type_c = voucher_type_c;
	}

	public Long getVoucher_inner_sn() {
		return voucher_inner_sn;
	}

	public void setVoucher_inner_sn(Long voucher_inner_sn) {
		this.voucher_inner_sn = voucher_inner_sn;
	}

	public Long getVoucher_details_inner_sn() {
		return voucher_details_inner_sn;
	}

	public void setVoucher_details_inner_sn(Long voucher_details_inner_sn) {
		this.voucher_details_inner_sn = voucher_details_inner_sn;
	}

	public Long getVendorId() {
		return vendorId;
	}

	public void setVendorId(Long vendorId) {
		this.vendorId = vendorId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Long getSpecificationId() {
		return specificationId;
	}

	public void setSpecificationId(Long specificationId) {
		this.specificationId = specificationId;
	}

	public Integer getProductType() {
		return productType;
	}

	public void setProductType(Integer productType) {
		this.productType = productType;
	}

	public Long getMdrf_inner_sn() {
		return mdrf_inner_sn;
	}

	public void setMdrf_inner_sn(Long mdrf_inner_sn) {
		this.mdrf_inner_sn = mdrf_inner_sn;
	}

	public String getMdrf_sn() {
		return mdrf_sn;
	}

	public void setMdrf_sn(String mdrf_sn) {
		this.mdrf_sn = mdrf_sn;
	}

	public String getHealth_care_sn() {
		return health_care_sn;
	}

	public void setHealth_care_sn(String health_care_sn) {
		this.health_care_sn = health_care_sn;
	}
}
