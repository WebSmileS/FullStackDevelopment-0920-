package com.yrt.common.dict;

/**
 * 医疗器械分类目录类别: `1-一类 2-二类 3-三类`
 */
public enum PrdCategory implements BaseIntEnum {

	CATEGORY1(0, "一类"),
	CATEGORY2(1, "二类"),
	CATEGORY3(2, "三类"),

	;

	private final int value;
	private final String text;

	private PrdCategory(int value, String text) {
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
