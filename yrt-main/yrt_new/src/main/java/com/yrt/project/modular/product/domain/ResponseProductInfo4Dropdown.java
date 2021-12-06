package com.yrt.project.modular.product.domain;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

public class ResponseProductInfo4Dropdown implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "厂商内部编号 ")
	private Long vendor_inner_sn;
	@ApiModelProperty(value = "厂商名称")
	private String vendor_name;
	@ApiModelProperty(value = "产品内部编号")
	private Long product_inner_sn;
	@ApiModelProperty(value = "产品名称")
	private String product_name;
	@ApiModelProperty(value = "注册证号")
	private String mdrf_sn;
	
	public Long getVendor_inner_sn() {
		return vendor_inner_sn;
	}
	public void setVendor_inner_sn(Long vendor_inner_sn) {
		this.vendor_inner_sn = vendor_inner_sn;
	}
	public String getVendor_name() {
		return vendor_name;
	}
	public void setVendor_name(String vendor_name) {
		this.vendor_name = vendor_name;
	}
	public Long getProduct_inner_sn() {
		return product_inner_sn;
	}
	public void setProduct_inner_sn(Long product_inner_sn) {
		this.product_inner_sn = product_inner_sn;
	}
	public String getProduct_name() {
		return product_name;
	}
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}
	public String getMdrf_sn() {
		return mdrf_sn;
	}
	public void setMdrf_sn(String mdrf_sn) {
		this.mdrf_sn = mdrf_sn;
	}
	
}
