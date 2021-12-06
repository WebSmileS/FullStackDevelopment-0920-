package com.yrt.common.dict;

/**
 * 库存产品列表(type): `0-常规 1-赠品`
 * @see GrnOdoDetailType
 */
public enum InventoryPrdType  implements BaseIntEnum {

	NORMAL(0, "常规"),
	GIFT(1, "赠品"),

	;

	private final int value;
	private final String text;

	private InventoryPrdType(int value, String text) {
		this.value = value;
		this.text = text;
	}

	@Override
	public int value() {
		return value;
	}

	@Override
	public String text() {
		return text;
	}

}

