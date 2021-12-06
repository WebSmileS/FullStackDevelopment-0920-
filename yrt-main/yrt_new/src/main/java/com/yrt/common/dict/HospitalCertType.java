package com.yrt.common.dict;

/**
 * 医院图片类型(certType):`0-其他 1-营业执照 2-医疗机构执业许可证 3-开户许可证`
 */
public enum HospitalCertType implements BaseIntEnum {

	OTHER(0, "其他"),
	LICENSE(1, "营业执照"),
	MDPL(2, "医疗机构执业许可证 "), // Medical Device Pratice License
	ACCOUNT_LICENSE(3, "开户许可证 "),

	;

	private final int value;
	private final String text;

	private HospitalCertType(int value, String text) {
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