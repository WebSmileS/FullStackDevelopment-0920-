package com.yrt.common.dict;

/**
 * 骨科合作意向状态: `0-无意向 1-有意向 2意向待确定`
 */
public enum OrthopaedicIntentionStatus implements BaseIntEnum {

	NO(0, "无意向"),
	YES(1, "有意向"),
	PENDING(2, "意向待确定"),

	;

	private final int value;
	private final String text;

	private OrthopaedicIntentionStatus(int value, String text) {
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