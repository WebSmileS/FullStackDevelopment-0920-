package com.yrt.common.dict;

/**
 * 仓库类别(type): `0-仓库 1-楼层 2-区域 3-托盘 4-货架组 5-货架 6-货位 7-储物柜 8-储物格`
 */
public enum WarehouseType implements BaseIntEnum {

	CK(0, "仓库"),
	LC(1, "楼层"),
	QY(2, "区域"),
	TP(3, "托盘"),
	HJZ(4, "货架组"),
	HJ(5, "货架"),
	HW(6, "货位"), // TODO: name
	CWG(7, "储物柜"),
	CWGE(8, "储物格"),
	
	;

	private final int value;
	private final String text;

	private WarehouseType(int value, String text) {
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
