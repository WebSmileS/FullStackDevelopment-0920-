package com.yrt.common.dict;

/**
 * 销售单状态: ~~0-草稿(不启用)<br>
 * 1-正式 <br>
 * 2-已审核 <br>
 * 3-已作废(不启用) <br>
 * 4-出库单已完全生成<br>
 * 5-已出库 <br>
 * 6-已配送 <br>
 * 7-已送达 <br>
 * 8-已收货<br>
 * 9-已开票 <br>
 * 10-已过账 <br>
 * 11-已结清<br>
 */
public enum SalesOrderStatus implements BaseIntEnum {
	DRAFT(0, "草稿(不启用)"),
	NORMAL(1, "正式"),
	AUDITED(2, "已审核"),
	OBSOLETED(3, "已作废(不启用)"),
	OUT_RECORD(4, "出库单已完全生成"),
	CHECKOUT(5, "已出库"), //ODO
	DELIVERIED(6, "已配送"),
	RECEIVED(7, "已送达"),
	CONFIRM_RECEIVED(8, "已收货"),
	INVOICED(9, "已开票"),
	POSTED(10, "已过账"),
	CLEARED(11, "已结清"),
	_TODO(12, "naming"),
	
	;

	private int value;
	private final String text;

	private SalesOrderStatus(int value, String text) {
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
