package com.yrt.common.dict;

/**
 * 厂商图片类型(certType):`0-其他 1-营业执照 2-医疗器械生产许可证 3-企业年度公示信息 4-印章印模 5-空白销售出库单(随货)
 * 6-质量体系调查表 7-合同供货方档案`
 */
public enum VendorCertType implements BaseIntEnum {

	OTHER(0, "其他"),
	LICENSE(1, "营业执照"),
	MDPL(2, "医疗器械生产许可证"), // Medical Device Produce License
	ANNUAL_INFO(3, "企业年度公示信息"),
	STAMP(4, "印章印模"),
	EMPTY_ODO(5, "空白销售出库单(随货)"),
	QS_DOC(6, "质量体系调查表"),
	QV_DOC(7, "合格供货方档案"),

	;

	private final int value;
	private final String text;

	private VendorCertType(int value, String text) {
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
