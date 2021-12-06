package com.yrt.common.dict;

/**
 * 产品使用方式: `0-工具 1-材料`
 */
public enum PrdUsageType implements BaseIntEnum {

	TOOLS(0, "工具"),
	MATERIAL(1, "材料"),

	;

	private final int value;
	private final String text;

	private PrdUsageType(int value, String text) {
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
