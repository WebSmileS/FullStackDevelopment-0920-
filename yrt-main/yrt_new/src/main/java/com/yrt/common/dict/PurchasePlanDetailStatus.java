package com.yrt.common.dict;

/**
 * 采购计划明细状态(status): `0-待处理 1-已终止 2-已生成采购订单`
 */
public enum PurchasePlanDetailStatus implements BaseIntEnum {
	PENDING(0, "待处理"),
	TERMINATED(1, "已终止"),
	ORDERED(2, "已生成采购订单"),
	
	;

	private int value;
	private final String text;

	private PurchasePlanDetailStatus(int value, String text) {
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