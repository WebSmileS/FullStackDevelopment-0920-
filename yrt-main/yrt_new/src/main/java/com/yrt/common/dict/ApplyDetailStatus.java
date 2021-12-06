package com.yrt.common.dict;

/**
 * 医院申领单产品明细状态(status): `0-申领中 1-已完全发放 2-已计划采购 3-已终止申领`
 */
public enum ApplyDetailStatus implements BaseIntEnum {

	APPLYING(0, "申领中"),
	GRANTED(1, "已完全发放"),
	PLANNED(2, "已计划采购 "), // Medical Device Permit operation License
	TERMINATED(3, "已终止申领 "),

	;

	private final int value;
	private final String text;

	private ApplyDetailStatus(int value, String text) {
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