package com.yrt.common.dict;

/**
 * 付款方式
 */
public enum PaymentMethod implements BaseIntEnum {

	REMITTANCE(0, "汇款"),
	CHECK(1, "支票"),
	OTHERS(2, "其他"),
	
	;

	private final int value;
	private final String text;

	private PaymentMethod(int value, String text) {
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
