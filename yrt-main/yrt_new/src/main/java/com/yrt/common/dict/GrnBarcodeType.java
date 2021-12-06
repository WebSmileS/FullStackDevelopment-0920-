package com.yrt.common.dict;

/**
 * 入库条码类型: `0-单一条码 1-多条码`
 */
public enum GrnBarcodeType implements BaseIntEnum {

	SINGLE(0, "单一条码"),
	MULTI(1, "多条码"),

	;

	private final int value;
	private final String text;

	private GrnBarcodeType(int value, String text) {
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