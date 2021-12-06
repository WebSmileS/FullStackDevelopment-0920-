package com.yrt.common.dict;

/**
 * 经销商编辑产品操作类型: `0-删除 1-新增 2-修改`
 */
public enum PrdEditOperation implements BaseIntEnum {

	DELETE(2, "删除"),
	INDERT(3, "新增"),
	UPDATE(4, "修改"),
	_TODO(5, "status-values: internal only?"),

	;

	private final int value;
	private final String text;

	private PrdEditOperation(int value, String text) {
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