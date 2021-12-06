package com.yrt.common.dict;

/**
 * 出/入 库单明细类型(type): `0-常规 1-赠品`
 * @see InventoryPrdType (duplicated)
 */
public enum GrnOdoDetailType implements BaseIntEnum {

	NORMAL(0, "常规"),
	GIFT(1, "赠品"),

	;

	private final int value;
	private final String text;

	private GrnOdoDetailType(int value, String text) {
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