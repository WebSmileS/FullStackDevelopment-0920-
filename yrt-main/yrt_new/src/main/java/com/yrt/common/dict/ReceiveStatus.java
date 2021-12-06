package com.yrt.common.dict;

/**
 * 收款状态
 */
public enum ReceiveStatus implements BaseIntEnum {

	ALL(0, "全部"),
	TO_BE_COLLECTED(1, "待收款"),
	RECEIVED(2, "已收款"),
	RECORDED(3, "已入账"),
	
	;

	private final int value;
	private final String text;

	private ReceiveStatus(int value, String text) {
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
