package com.yrt.common.dict;

/**
 * 员工学历(degree): `0-文盲 1-小学 2-初中 3-高中 4-职高 5-大专 6-大学 7-硕士 8-博士 9-博士后`
 */
public enum EducationDegree implements BaseIntEnum {

	DEGREE0(0, "文盲"),
	DEGREE1(1, "小学"),
	DEGREE2(2, "初中"),
	DEGREE3(3, "高中"),
	DEGREE4(4, "职高"),
	DEGREE5(5, "大专"),
	DEGREE6(6, "大学"),
	DEGREE7(7, "硕士"),
	DEGREE8(8, "博士"),
	DEGREE9(9, "博士后"),
	_TODO(10, "naming"),

	;

	private final int value;
	private final String text;

	private EducationDegree(int value, String text) {
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