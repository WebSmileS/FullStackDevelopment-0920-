package com.yrt.common.dict;

/**
 * 骨科手术包产品明细类型: `0-工具 1-材料`
 * 
 * @see PrdUsageType
 */
public enum OrthopaedicOpPackageType implements BaseIntEnum {

	TOOLS(0, "工具"),
	MATERIAL(1, "材料"),

	;

	private final int value;
	private final String text;

	private OrthopaedicOpPackageType(int value, String text) {
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