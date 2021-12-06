package com.yrt.common.dict;

/**
 * 采购订单明细状态: `0-处理中 1-已完全处理 2-已计划采购 3-已终止处理`
 */
public enum PurchaseOrderDetailStatus implements BaseIntEnum {
	PENDING(0, "处理中"),
	FINISHED(1, "已完全处理"),
	PLANNED(2, "已计划采购"),
	TERMINATED(3, "已终止处理"),
	
	;
	
	
	private int value;
	private final String text;

	private PurchaseOrderDetailStatus(int value, String text) {
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
