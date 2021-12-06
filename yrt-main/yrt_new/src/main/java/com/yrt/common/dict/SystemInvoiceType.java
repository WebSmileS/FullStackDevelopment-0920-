package com.yrt.common.dict;

/**
 * 系统发票类型(SystemInvoiceType):`0-GRN_INVOICE 1-ODO_INVOICE`
 */
public enum SystemInvoiceType implements BaseIntEnum {
	GRN_INVOICE(0, "入库发票"),
	ODO_INVOICE(1, "出库发票"),
	;

	private final int value;
	private final String text;

	private SystemInvoiceType(int value, String text) {
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