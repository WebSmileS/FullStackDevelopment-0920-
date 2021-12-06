package com.yrt.common.dict;

/**
 * 经营方式(operation_mode):`0-其他 1-批发 2-零售 3-制作 4-咨询 5-租赁 6-代理 7-采掘`
 */
public enum OperationMode implements BaseIntEnum {

	OTHER(0, "其他"),
	WHOLESALE(1, "批发"),
	RETAIL(2, "零售"),
	MANUFACTURE(3, "制作"),
	ADVISORY(4, "咨询"), //consultant
	RENT(5, "租赁"),//
	PROXY(5, "代理"),
	MINING(5, "采掘"),

	;

	private final int value;
	private final String text;

	private OperationMode(int value, String text) {
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
