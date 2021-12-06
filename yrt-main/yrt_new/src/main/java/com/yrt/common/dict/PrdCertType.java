package com.yrt.common.dict;

/**
 * 产品图片类型(certType):`0-其他 1-产品`~~2-注册证 3-注册证登记表~~`4-质量检测报告`
 */
public enum PrdCertType implements BaseIntEnum {

	OTHER(0, "其他"),
	PRODUCT(1, "产品"),
	MDRF(2, "注册证"),
	MDRF_SHEET(3, "注册证登记表"),
	QS(4, "质量检测报告"),

	;

	private final int value;
	private final String text;

	private PrdCertType(int value, String text) {
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