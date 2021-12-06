package com.yrt.common.dict;

/**
 * 预警类型(WarningType):`0-机构资证预警 1-产品注册证预警 2-产品效期预警`
 */
public enum WarningType implements BaseIntEnum {

	CERT(0, "机构资证预警"),
	MDRF(1, "产品注册证预警"),
	VALIDITY(2, "产品效期预警"),

	;

	private final int value;
	private final String text;

	private WarningType(int value, String text) {
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
