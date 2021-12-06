package com.yrt.common.dict;

/**
 * 关联单据状态:
 * 1-待完成 <br>
 * 2-已完成 <br>
 */
public enum TransferVoucherStatus implements BaseIntEnum {

	AWAITING(1, "待完成"),
	COMPLETION(2, "已完成"),
	
	;

	private int value;
	private final String text;

	private TransferVoucherStatus(int value, String text) {
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
