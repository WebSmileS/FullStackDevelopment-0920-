package com.yrt.common.dict;

/**
 * 经销商图片类型(certType):`0-其他 1-营业执照 2-医疗器械经营许可证 3-第二类医疗器械经营备案凭证 4-企业年度公示信息 5-印章印模
 * 6-空白销售出库单 7-质量体系调查表 8-合格供货方档案`
 */
public enum DealerCertType implements BaseIntEnum {

	OTHER(0, "其他"),
	LICENSE(1, "营业执照"),
	MDPL(2, "医疗器械经营许可证 "), // Medical Device Permit operation License
	MD2_DOC(3, "第二类医疗器械经营备案凭证 "),
	ANNUAL_INFO(4, "企业年度公示信息"),
	STAMP(5, "印章印模"),
	EMPTY_ODO(6, "空白销售出库单"),
	QS_DOC(7, "质量体系调查表"),
	QV_DOC(8, "合格供货方档案"),

	;

	private final int value;
	private final String text;

	private DealerCertType(int value, String text) {
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