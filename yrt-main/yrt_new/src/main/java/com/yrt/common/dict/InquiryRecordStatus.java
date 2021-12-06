package com.yrt.common.dict;

/**
 * 询价状态(status): `0-询价草稿 1-询价 2-回复草稿 3-已回复`
 */
public enum InquiryRecordStatus implements BaseIntEnum {

	INQUIRY_DRAFT(0, "询价草稿"),
	INQUIRING(1, "询价"),
	REPLY_DRAFT(2, "回复草稿 "),
	REPLIED(2, "已回复 "),

	;

	private final int value;
	private final String text;

	private InquiryRecordStatus(int value, String text) {
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