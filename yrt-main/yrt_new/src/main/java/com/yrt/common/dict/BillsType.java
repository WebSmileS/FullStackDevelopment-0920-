package com.yrt.common.dict;

/**
 * 单据类型(billsType):`0-DealerDealer 1-DealerVendor 2-DealerHospital 3-HospitalHospital 4-HospitalDealer 5-HospitalVendor`
 */
public enum BillsType implements BaseIntEnum {

	DD(0, "经销商TO经销商"),
	DV(1, "经销商TO厂商"),
	DH(2, "经销商TO医院"),
	HH(3, "医院TO医院"),
	HD(4, "医院TO经销商"),
	HV(5, "医院TO厂商"),
	VD(6, "厂商TO经销商"),
	VH(7, "厂商TO医院"),
	VV(8, "厂商TO厂商"),
	;

	private final int value;
	private final String text;

	private BillsType(int value, String text) {
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