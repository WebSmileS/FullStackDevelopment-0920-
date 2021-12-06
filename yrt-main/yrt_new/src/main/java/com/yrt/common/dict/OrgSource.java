package com.yrt.common.dict;

/**
 * 机构信息来源
 */
public enum OrgSource implements BaseIntEnum {

	PLATFORM(0, "来自平台"),
	HOSPITAL(1, "来自医院"),
	DEALEAR(2, "来自经销商"),
	VENDOR(3, "来自厂商"),
	
	;

	private final int value;
	private final String text;

	private OrgSource(int value, String text) {
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
