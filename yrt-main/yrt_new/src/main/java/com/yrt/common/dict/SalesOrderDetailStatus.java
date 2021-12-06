package com.yrt.common.dict;

/**
 * 销售单明细状态: `0-待生成出库单 1-出库单已完全生成`
 */
public enum SalesOrderDetailStatus implements BaseIntEnum {
	PENDING(0, "待生成出库单"),
	FINISHED(1, "出库单已完全生成"),
	
	;

	private int value;
	private final String text;

	private SalesOrderDetailStatus(int value, String text) {
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
