package com.yrt.common.dict;

/**
 * 发票状态
 */
public enum InvoiceStatus implements BaseIntEnum {

	ALL(0, "全部"),
	DRAFT(1, "草稿"),
	PENDING(2, "待审核"),
	NOT_WRITTEN_OFF(3, "未核销"),
	PARTIAL_WRITE_OFF(4, "部分核销"),
	WRITTEN_OFF(5, "已核销"),
	
	;

	private final int value;
	private final String text;

	private InvoiceStatus(int value, String text) {
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
