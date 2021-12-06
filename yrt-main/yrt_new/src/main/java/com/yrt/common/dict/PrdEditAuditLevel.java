package com.yrt.common.dict;

/**
 * 经销商编辑产品操作审核级别: `0-一级 1-二级 2-三级 3-四级`
 */
public enum PrdEditAuditLevel implements BaseIntEnum {

	LEVEL1(0, "一级"),
	LEVEL2(1, "二级"),
	LEVEL3(2, "三级"),
	LEVEL4(3, "四级"),

	;

	private final int value;
	private final String text;

	private PrdEditAuditLevel(int value, String text) {
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