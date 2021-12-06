package com.yrt.common.dict;

/**
 * 骨科手术类别: `0-颅骨 1-脊柱 2-关节 3-创伤`
 */
public enum OrthopaedicOperationType implements BaseIntEnum {

	TYPE0(0, "颅骨"),
	TYPE1(1, "脊柱"),
	TYPE2(2, "关节"),
	TYPE3(3, "创伤"),
	_TODO(4, "naming"),

	;

	private final int value;
	private final String text;

	private OrthopaedicOperationType(int value, String text) {
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