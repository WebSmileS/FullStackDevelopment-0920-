package com.yrt.project.api.business.surgery.cust.vo;

import com.yrt.common.dict.BaseIntEnum;

public enum SearchScope implements BaseIntEnum {

	ALL("全部"),

	/**
	 * platform ois/kit only
	 */
	PLATFORM("平台"),

	/**
	 * for Dealer(my owned only); for Hospital(kit/ois owned by dealers respective)
	 */
	PRIVATE("私有"),

	;

	private final String text;

	private SearchScope(String text) {
		this.text = text;
	}

	@Override
	public String text() {
		return text;
	}
}
