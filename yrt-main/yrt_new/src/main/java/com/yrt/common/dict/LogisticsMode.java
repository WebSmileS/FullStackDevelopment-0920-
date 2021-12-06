package com.yrt.common.dict;

/**
 * 物流方式
 */
public enum LogisticsMode implements BaseIntEnum {

	SELF(0, "自送"),
	THIRD(1, "第三方物流"),
	;

	private final int value;
	private final String text;

	private LogisticsMode(int value, String text) {
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
