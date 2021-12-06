package com.yrt.common.dict;

/**
 * 医院发放单状态(status): `0-可发放 1-出库单已完全生成 2-已领取`
 */
public enum GrantRecordStatus implements BaseIntEnum {
	PENDING(0, "可发放"),
	ODO_GEN(1, "出库单已完全生成"),
	FINISHED(2, "已领取"),
	
	;

	private int value;
	private final String text;

	private GrantRecordStatus(int value, String text) {
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
