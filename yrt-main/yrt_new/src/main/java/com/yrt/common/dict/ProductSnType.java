package com.yrt.common.dict;

/**
 * 产品编号类型(productSnType):`0-医保编号 1-阳光流水号`
 */
public enum ProductSnType implements BaseIntEnum {

	HEALTH_CARE_SN(0, "医保编号"),
	SUN_SN(1, "阳光流水号"),
	;

	private final int value;
	private final String text;

	private ProductSnType(int value, String text) {
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