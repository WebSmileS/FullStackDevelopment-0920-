package com.yrt.project.modular.surgery;

import com.yrt.common.dict.BaseIntEnum;

/**
 * 手术计划 状态
 */
public enum OperationPlanStatus implements BaseIntEnum {
	DRAFT(0, "草稿"),
	CONFIRMED(1, "确认(手术包)"),
	PUBLISHED(2, "发布(指定各手术包经销商)"),
	CLOSED(3, "关闭"),

	;

	private final int value;
	private final String text;

	private OperationPlanStatus(int value, String text) {
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

	public boolean canEdit() {
		return this == DRAFT || this == CONFIRMED;
	}

	public boolean canDelete() {
		return this == DRAFT || this == CONFIRMED;
	}

	public boolean canAddOis() {
		return this != CLOSED;
	}
}
