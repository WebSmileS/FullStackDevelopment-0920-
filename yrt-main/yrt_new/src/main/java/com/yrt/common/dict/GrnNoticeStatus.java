package com.yrt.common.dict;

/**
 * 通知单状态
 */
public enum GrnNoticeStatus implements BaseIntEnum {

	TO_BE_RECEIVED(0, "未收货"),
	RECEIVED(1, "已收货"),
	CANCEL(2, "作废"),
	;

	private final int value;
	private final String text;

	private GrnNoticeStatus(int value, String text) {
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
