package com.yrt.common.dict;

/**
 * 经销商编辑产品状态: `2-草稿 3-待审核 4-审核未通过`
 * <p>
 * 0,1???
 */
public enum PrdEditStatus implements BaseIntEnum {

	DRAFT(2, "草稿"),
	PENDING(3, "待审核"),
	AUDITED(4, "审核未通过"),
	_TODO(5, "status-values: internal only?"),

	;

	private final int value;
	private final String text;

	private PrdEditStatus(int value, String text) {
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