package com.yrt.common.dict;

/**
 * 通知单状态
 */
public enum OdoNoticeStatus implements BaseIntEnum {

	TO_BE_DELIVER(0, "未发货"),
	DELIVER(1, "已发货"),
	CANCEL(2, "取消"),
	FINISH(3, "完成"),
	;

	private final int value;
	private final String text;

	private OdoNoticeStatus(int value, String text) {
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
