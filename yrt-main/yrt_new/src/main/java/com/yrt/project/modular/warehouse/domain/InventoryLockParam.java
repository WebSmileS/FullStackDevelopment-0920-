package com.yrt.project.modular.warehouse.domain;

import java.math.BigDecimal;

import org.springframework.beans.BeanUtils;

import lombok.Getter;
import lombok.Setter;

public class InventoryLockParam extends InventoryKey {
	
	public static InventoryLockParam of(InventoryKey key, BigDecimal quantity) {
		InventoryLockParam ret = new InventoryLockParam();
		BeanUtils.copyProperties(key, ret);
		ret.setQuantity(quantity);
		return ret;
	}

	/**
	 * 数量(最小单位)
	 */
	@Getter @Setter
	private BigDecimal quantity;
}
