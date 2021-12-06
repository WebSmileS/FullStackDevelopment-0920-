package com.yrt.common.dict;

/**
 * 机构类型(enterprise_type):`0-非公司企业法人 1-有限责任公司 2-股份有限责任公司 3-个体工商户 4-私营独资企业
 * 5-私营合伙企业`
 */
public enum EnterpriseType implements BaseIntEnum {

	TYPE0(0, "非公司企业法人"),
	TYPE1(1, "有限责任公司"),
	TYPE2(2, "股份有限责任公司"),
	TYPE3(3, "个体工商户"),
	TYPE4(4, "私营独资企业"),
	TYPE5(5, "私营合伙企业"),
	_TODO(6, "naming"),

	;

	private final int value;
	private final String text;

	private EnterpriseType(int value, String text) {
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
