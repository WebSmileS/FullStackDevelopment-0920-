package com.yrt.common.dict;

/**
 * 员工证件类别(personal_certificate_type): `0-身份证 1-护照`
 */
public enum PersonCertificateType implements BaseIntEnum {

	ID(0, "身份证"),
	PASSPORT(1, "护照"),

	;

	private final int value;
	private final String text;

	private PersonCertificateType(int value, String text) {
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
