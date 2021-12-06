package com.yrt.project.modular.warehouse.domain;

import com.yrt.common.dict.PrdType;

import lombok.Data;

/**
 * 单位换算倍率 key.
 */
@Data(staticConstructor = "of")
public class ProductUnitConvertKey {
	
	private final PrdType prdType;
	/**
	 * product_vendor_inner_sn/product_dealer_inner_sn: 由 {@link #prdType} 决定
	 */
	private final Long vendorId;
	private final Long productId;
	private final Long specificationId;
	private final Long unitId;
	
}
