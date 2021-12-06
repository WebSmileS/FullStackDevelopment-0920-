package com.yrt.common.dict;

/**
 * 出库单状态: `0-正式 1-已审核 2-已出库(已过账) 3-已出库复核`
 */
public enum OdoStatus implements BaseIntEnum {

	NORMAL(0, "正式"),
	AUDITED(1, "已审核"),
	OUT_POSTED(2, "已出库(已过账)"),
	OUT_AUDITED(3, "已出库复核"),
	// 4 作废
	// 5 红冲
	;

	private final int value;
	private final String text;

	private OdoStatus(int value, String text) {
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