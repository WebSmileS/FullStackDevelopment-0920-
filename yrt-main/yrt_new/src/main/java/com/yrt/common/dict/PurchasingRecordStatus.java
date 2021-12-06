package com.yrt.common.dict;

/**
 * 采购记录状态
 */
public enum PurchasingRecordStatus implements BaseIntEnum {

	TO_BE_VERIFY(0, "未审核"),
	TO_BE_CONFIRM(1, "待确认"),
	EXECUTING(2, "执行中"),
	FINISHED(3, "完成"),
	CANCEL(5, "终止"),
	KILL(6,"强行终止"),
	;

	private final int value;
	private final String text;

	private PurchasingRecordStatus(int value, String text) {
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
