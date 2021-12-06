package com.yrt.common.dict;

/**
 * 医院申领单(主记录)状态(status)
 */
public enum ApplyRecordStatus implements BaseIntEnum {

	//0-草稿 1-申领 2-全部发放(自然终止) 3-申领终止(人为终止)
	DRAFT(0, "草稿"),
	APPLYING(1, "未发放 "),
	FINISHED(2, "全部发放(自然终止) "),
	TERMINATED(3, "申领终止(人为终止) "),

	;

	private final int value;
	private final String text;

	private ApplyRecordStatus(int value, String text) {
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
