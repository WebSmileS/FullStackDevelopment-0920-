package com.yrt.project.api.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class BaseQuantities implements Serializable {

	private static final long serialVersionUID = 1L;

	private String productName;
	
	private String specification;
	
	private String unitName;
	
	private Long unitId;
	
	private BigDecimal lockedQuantity = BigDecimal.ZERO;
	
	public static BaseQuantities of(String productName, String spec, String unitName,Long unitId, BigDecimal lockedQuantity) {
		BaseQuantities base = new BaseQuantities();
		base.setProductName(productName);
		base.setSpecification(spec);
		base.setUnitName(unitName);
		base.setUnitId(unitId);
		base.setLockedQuantity(lockedQuantity);
		return base;
	}

	public Long getUnitId() {
		return unitId;
	}

	public void setUnitId(Long unitId) {
		this.unitId = unitId;
	}

	public BigDecimal getLockedQuantity() {
		return lockedQuantity;
	}
	
	public BigDecimal addLockedQuantity(BigDecimal quantity) {
		return this.lockedQuantity = this.lockedQuantity.add(quantity);
	}

	public void setLockedQuantity(BigDecimal lockedQuantity) {
		this.lockedQuantity = lockedQuantity;
	}
	
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getSpecification() {
		return specification;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
}
