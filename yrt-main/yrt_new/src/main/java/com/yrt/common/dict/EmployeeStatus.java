package com.yrt.common.dict;

/**
 * 员工状态(status): `0-试用期 1-在职 2-离职 3-停薪留职`
 */
public enum EmployeeStatus implements BaseIntEnum {

	PROBATION(0, "试用期"),
	INCUMBENCY(1, "在职"),
	QUIT(2, "离职"),
	UNPAID_LEAVE(3, "停薪留职"),

	;

	private final int value;
	private final String text;

	private EmployeeStatus(int value, String text) {
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