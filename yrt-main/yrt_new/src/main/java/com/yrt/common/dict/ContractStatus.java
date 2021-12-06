package com.yrt.common.dict;

/**
 * 合同状态(status): `0-草稿 1-审批进行中 2-审批未通过 3-审批通过 4-有效履行中 5-自然终止 6-提前终止 7-超期履行`
 */
public enum ContractStatus implements BaseIntEnum {

	DRAFT(0, "草稿"),
	AUDITING(1, "审批进行中"),
	NOPASS(2, "审批未通过 "), 
	PASS(3, "审批通过 "),
	EXECUTING(4, "有效履行中"),
	FINISHED(5, "自然终止"),
	TERMINATED(6, "提前终止"),
	EXTENDED(7, "超期履行"),

	;

	private final int value;
	private final String text;

	private ContractStatus(int value, String text) {
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