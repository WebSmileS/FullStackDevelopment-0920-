package com.yrt.common.dict;

/**
 * 付款状态
 */
public enum PaymentStatus implements BaseIntEnum {

	ALL(0, "全部"),
	TO_BE_PAID(1, "待付款"),
	PAID(2, "已付款"),
	RECORDED(3, "已入账"),
	
	;

	private final int value;
	private final String text;

	private PaymentStatus(int value, String text) {
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
