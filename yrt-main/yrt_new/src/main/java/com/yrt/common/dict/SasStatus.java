package com.yrt.common.dict;

/**
 * 储位分配单状态: `0-待放置 1-部分放置 2-作业结束`
 */
public enum SasStatus implements BaseIntEnum {
	PENDING(0, "待放置"),
	PARTIAL(1, "部分放置"),
	FINISHED(2, "作业结束"),
	
	;

	private int value;
	private final String text;

	private SasStatus(int value, String text) {
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
