package com.yrt.common.dict;

/**
 * 系统/机构 类型
 * alias OrgType
 */
public enum SystemType implements BaseIntEnum {

	PLATFORM(0, "平台"),
	HOSPITAL(1, "医院"),
	DEALER(2, "经销商"),
	VENDOR(3, "厂商"),
	
	;

	private final int value;
	private final String text;

	private SystemType(int value, String text) {
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
