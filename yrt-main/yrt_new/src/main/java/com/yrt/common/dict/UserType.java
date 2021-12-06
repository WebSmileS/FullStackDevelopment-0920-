package com.yrt.common.dict;

/**
 * 用户/账号 类型
 */
public enum UserType implements BaseIntEnum {
	NORMAL(0, "普通用户"),
	ADMIN(1, "管理员"),
	
	;

	private int value;
	private final String text;

	private UserType(int value, String text) {
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
