package com.yrt.common.dict;

/**
 * 产品类型(有无证书)
 * 产品类型(type): `0-医疗器械 1-非医疗器械` 
 * TODO: ???
 */
public enum PrdType implements BaseIntEnum {

	DELEAR_PRD(0, "经销商自建产品"),
	VENDOR_PRD(1, "厂商产品"),
	
	;

	private final int value;
	private final String text;

	private PrdType(int value, String text) {
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
