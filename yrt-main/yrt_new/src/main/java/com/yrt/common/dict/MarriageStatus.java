package com.yrt.common.dict;

/**
 * 员工婚姻状况(marriage_status): `0-未婚 1-已婚 2-离异 3-丧偶`
 */
public enum MarriageStatus implements BaseIntEnum {

	UNMARRIED(0, "未婚"),
	MARRIED(1, "已婚"),
	DIVORCED(2, "离异"),
	WIDOWED(3, "丧偶"),
	_TODO(4, "naming"),

	;

	private final int value;
	private final String text;

	private MarriageStatus(int value, String text) {
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
