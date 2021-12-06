package com.yrt.project.modular.product.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import com.yrt.project.modular.common.domain.ResponseProductInfo;

import io.swagger.annotations.ApiModelProperty;

public class ApplyResponseProductInfo extends ResponseProductInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "医院内部编号 ")
	private Long hospital_inner_sn;
	
	@ApiModelProperty(value = "最小包装单位价格")
	private BigDecimal min_unit_price;
	
	@ApiModelProperty(value = "最小包装单位内部编号")
	private Long min_unit_inner_sn;
	
	@ApiModelProperty(value = "最小包装单位")
	private String min_unit;
	
	@ApiModelProperty(value = "产品图片URL")
	private String image;

	public Long getHospital_inner_sn() {
		return hospital_inner_sn;
	}

	public void setHospital_inner_sn(Long hospital_inner_sn) {
		this.hospital_inner_sn = hospital_inner_sn;
	}

	public BigDecimal getMin_unit_price() {
		return min_unit_price;
	}

	public void setMin_unit_price(BigDecimal min_unit_price) {
		this.min_unit_price = min_unit_price;
	}

	public Long getMin_unit_inner_sn() {
		return min_unit_inner_sn;
	}

	public void setMin_unit_inner_sn(Long min_unit_inner_sn) {
		this.min_unit_inner_sn = min_unit_inner_sn;
	}

	public String getMin_unit() {
		return min_unit;
	}

	public void setMin_unit(String min_unit) {
		this.min_unit = min_unit;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

}
