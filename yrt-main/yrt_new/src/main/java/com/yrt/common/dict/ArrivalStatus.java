package com.yrt.common.dict;

/**
 * 收货单状态: 
 * 0-未收货<br>
 * 1-已收货<br>
 * 2-已复核<br>
 */
public enum ArrivalStatus implements BaseIntEnum {
	NOT_RECEIVED(0, "未收货"),
	RECEIVED(1, "已收货"),
	REVIEWED(2, "已复核"),
	
	;

	private int value;
	private final String text;

	private ArrivalStatus(int value, String text) {
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
