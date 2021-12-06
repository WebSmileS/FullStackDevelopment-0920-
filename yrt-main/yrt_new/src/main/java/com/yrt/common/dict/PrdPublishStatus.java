package com.yrt.common.dict;

/**
 * 产品发布状态
 */
public enum PrdPublishStatus implements BaseIntEnum {

	PENDING(0, "未发布"),
	PUBLIC(1, "已发布/公开"),
	
	;

	private final int value;
	private final String text;

	private PrdPublishStatus(int value, String text) {
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
