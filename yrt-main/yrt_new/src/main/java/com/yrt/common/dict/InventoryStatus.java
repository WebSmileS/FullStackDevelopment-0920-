package com.yrt.common.dict;

/**
 * 库存产品状态: `0-停售 1-正常`
 */
public enum InventoryStatus implements BaseIntEnum {

	STOPPED(0, "停售"),
	NORMAL(1, "正常"),

	;

	private final int value;
	private final String text;

	private InventoryStatus(int value, String text) {
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
