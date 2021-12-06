package com.yrt.common.dict;

/**
 * 储位分配单明细状态: `0-待放置 1-已部分放置 2-已全部放置`
 */
public enum SasDetailStatus implements BaseIntEnum {
	PENDING(0, "待放置"),
	PARTIAL(1, "部分放置"),
	FINISHED(2, "全部放置"),
	
	;

	private int value;
	private final String text;

	private SasDetailStatus(int value, String text) {
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
