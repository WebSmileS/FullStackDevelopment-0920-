package com.yrt.common.dict;

/**
 * 机构 审核状态 README.md: "0-未认证 1-用户申请认证 2-认证通过 3-认证未通过" (描述有误？)
 */
public enum OrgAuditStatus implements BaseIntEnum {

	PENDING(0, "待审核"),
	PASS(1, "审核通过"), // NotAuditOrganization 描述有误("注册状态 0-待审核 1-审核失败")
	NOPASS(2, "审核不通过"),

	;
	private int value;
	private final String text;

	private OrgAuditStatus(int value, String text) {
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
