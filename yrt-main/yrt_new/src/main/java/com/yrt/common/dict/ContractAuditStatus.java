package com.yrt.common.dict;

/**
 * 合同审批状态(status): `0-发起 1-未处理 2-处理进行中 3-同意 4-不同意`
 */
public enum ContractAuditStatus implements BaseIntEnum {

	START(0, "发起"),
	PENDING(1, "未处理"),
	AUDITING(2, "处理进行中 "),
	PASS(3, "同意 "),
	NOPASS(4, "不同意"),
	_TODO(5, "naming"),

	;

	private final int value;
	private final String text;

	private ContractAuditStatus(int value, String text) {
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