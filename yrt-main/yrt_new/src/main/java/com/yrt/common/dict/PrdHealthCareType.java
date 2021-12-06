package com.yrt.common.dict;

/**
 * 产品医保类别(health_care_type):`0-A型 1-B型 2-C型`
 */
public enum PrdHealthCareType implements BaseIntEnum {

	A(0, "A型"),
	B(1, "B型"),
	C(2, "C型"),

	;

	private final int value;
	private final String text;

	private PrdHealthCareType(int value, String text) {
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