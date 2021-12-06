package com.yrt.common.dict;

/**
 * 仓库人员类型(type): `0-仓库主管 1-拣货员 2-上货员 3-验货员 4-出库员 5-入库员`
 */
public enum WharehouseEmployeeType implements BaseIntEnum {

	ZG(0, "仓库主管"),
	JH(1, "拣货员"),
	SH(2, "上货员"),
	YH(3, "验货员"),
	CK(4, "出库员"), // TODO: name
	RK(5, "入库员"),
	
	;

	private final int value;
	private final String text;

	private WharehouseEmployeeType(int value, String text) {
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
