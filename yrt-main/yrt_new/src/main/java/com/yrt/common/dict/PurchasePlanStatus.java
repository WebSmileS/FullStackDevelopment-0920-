package com.yrt.common.dict;

/**
 * 采购计划状态(status): `0-草稿 1-正式 2-已审核 3-已终止 4-订单已生成`
 */
public enum PurchasePlanStatus implements BaseIntEnum {
	DRAFT(0, "草稿"),
	NORMAL(1, "正式"),
	AUDITED(2, "已审核"),
	TERMINATED(3, "已终止"),
	ORDERED(4, "订单已生成"),
	
	;

	private int value;
	private final String text;

	private PurchasePlanStatus(int value, String text) {
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
