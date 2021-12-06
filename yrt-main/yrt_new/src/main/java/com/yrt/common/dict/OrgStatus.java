package com.yrt.common.dict;

/**
 * 机构状态
 */
public enum OrgStatus implements BaseIntEnum {

	LOCKED(0, "锁定"),
	NORMAL(1, "正常"),
	
	;

	private final int value;
	private final String text;

	private OrgStatus(int value, String text) {
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
