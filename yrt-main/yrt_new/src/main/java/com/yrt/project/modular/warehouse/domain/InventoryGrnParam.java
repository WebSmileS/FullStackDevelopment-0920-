package com.yrt.project.modular.warehouse.domain;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.BeanUtils;

import lombok.Getter;
import lombok.Setter;

public class InventoryGrnParam extends InventoryKey {

	public static InventoryGrnParam of(InventoryKey key, String productVendorName, String productName, String specification, String unitName,
			Long unitId, Date productDate, Date overdueDate, BigDecimal minUnitQuantity, BigDecimal averagePrice) {
		InventoryGrnParam ret = new InventoryGrnParam();
		BeanUtils.copyProperties(key, ret);
		ret.setProductVendorName(productVendorName);
		ret.setProductName(productName);
		ret.setSpecification(specification);
		ret.setMinUnitName(unitName);
		ret.setUnitId(unitId);
		ret.setProductDate(productDate);
		ret.setOverdueDate(overdueDate);
		ret.setMinUnitQuantity(minUnitQuantity);
		ret.setAveragePrice(averagePrice);
		return ret;
	}

	/**
	 * 产品单位ID
	 */
	@Getter @Setter
	private Long unitId;
	/**
	 * 产品厂家名称
	 */
	@Getter @Setter
	private String productVendorName;
	/**
	 * 产品名称
	 */
	@Getter @Setter
	private String productName;
	/**
	 * 产品规格型号
	 */
	@Getter @Setter
	private String specification;
	/**
	 * 产品单位名称
	 */
	@Getter @Setter
	private String minUnitName;
	/**
	 * 生产时间
	 */
	@Getter @Setter
	private Date productDate;
	/**
	 * 失效时间
	 */
	@Getter @Setter
	private Date overdueDate;
	/**
	 * 平均单价
	 */
	@Getter @Setter
	private BigDecimal averagePrice;
	
	/**
	 * 数量(最小单位)
	 */
	@Getter @Setter
	private BigDecimal minUnitQuantity;
	
}
