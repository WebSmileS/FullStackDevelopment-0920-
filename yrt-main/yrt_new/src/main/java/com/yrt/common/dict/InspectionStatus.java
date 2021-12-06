package com.yrt.common.dict;

/**
 * 验货单状态: `0-未验货 1-已验货 2-已复核`
 */
public enum InspectionStatus implements BaseIntEnum {

	NOT_INSPECTED(0, "未验货"),
	INSPECTED(1, "已验货"),
	REVIEWED(2, "已复核"),

	;

	private final int value;
	private final String text;

	private InspectionStatus(int value, String text) {
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
