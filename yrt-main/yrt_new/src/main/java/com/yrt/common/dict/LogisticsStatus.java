package com.yrt.common.dict;

/**
 * 物流信息状态
 */
public enum LogisticsStatus implements BaseIntEnum {

	DRAFTD(0, "未确认"),
	CONFIRMED(1, "已确认"),
	;

	private final int value;
	private final String text;

	private LogisticsStatus(int value, String text) {
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
