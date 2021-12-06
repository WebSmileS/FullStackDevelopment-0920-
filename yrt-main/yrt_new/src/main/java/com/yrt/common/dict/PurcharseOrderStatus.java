package com.yrt.common.dict;

/**
 * 采购订单状态: `0-草稿 1-正式 2-已审批 3-确认 4-已终止 5-已完结 6-未通过(未启用)`
 */
public enum PurcharseOrderStatus  implements BaseIntEnum {
	DRAFT(0, "草稿"),
	NORMAL(1, "正式"),
	AUDITED(2, "已审批"),
	CONFIRMED(3, "已审批"),
	TERMINATED(4, "已终止"),
	FINISHED(5, "已完结"),
	REJECTED(6, "未通过(未启用)"),
	
	;

	private int value;
	private final String text;

	private PurcharseOrderStatus(int value, String text) {
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
