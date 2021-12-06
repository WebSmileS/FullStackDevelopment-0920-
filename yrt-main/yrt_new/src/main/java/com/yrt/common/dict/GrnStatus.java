package com.yrt.common.dict;

/**
 * 入库单状态: `0-正式 1-已审核 2-已入账 3-已出库复核`
 */
public enum GrnStatus implements BaseIntEnum {

	NORMAL(0, "正式"),
	AUDITED(1, "已审核"),
	IN_RECORD(2, "已入账 "),
	OUT_CHECK(3, "已出库复核 "),
	// 4 作废
	// 5 红冲
	;

	private final int value;
	private final String text;

	private GrnStatus(int value, String text) {
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