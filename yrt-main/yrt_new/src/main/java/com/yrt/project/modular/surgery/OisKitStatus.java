package com.yrt.project.modular.surgery;

import com.yrt.common.dict.BaseIntEnum;

/**
 * 骨科 手术包/工具包 状态
 */
public enum OisKitStatus implements BaseIntEnum {
	INVALID(0, "无效"),
	VALID(1, "有效"),

	;

	private final int value;
	private final String text;

	private OisKitStatus(int value, String text) {
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
