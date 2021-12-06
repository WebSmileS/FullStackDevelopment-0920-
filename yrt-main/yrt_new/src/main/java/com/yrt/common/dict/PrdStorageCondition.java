package com.yrt.common.dict;

/**
 * 产品存储条件(storage_condition):`0-其他 1-常温 2-保温 3-阴凉 4-冷藏 5-冷冻 6-无菌 7- 特储`
 */
public enum PrdStorageCondition implements BaseIntEnum {

	OTHER(0, "其他"),
	NORMAL(1, "常温"),
	WARM(2, "保温"),
	COOL(3, "阴凉"),
	COLD(4, "冷藏"),
	FREEZING(5, "冷冻"),
	GERM_FREE(6, "无菌"), // STERILE
	SPECIAL(7, "特储"),

	;

	private final int value;
	private final String text;

	private PrdStorageCondition(int value, String text) {
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