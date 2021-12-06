package com.yrt.common.dict;

/**
 * 发票类型(InvoiceType):`0-GENERAL_INVOICE 1-VAT_INVOICE 2-CASH_FLOW_INVOICE`
 */
public enum InvoiceType implements BaseIntEnum {

	GENERAL_INVOICE(0, "普通发票"),
	VAT_INVOICE(1, "增值税发票"),
	CASH_FLOW_INVOICE(2, "资金往来发票"),
	;

	private final int value;
	private final String text;

	private InvoiceType(int value, String text) {
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