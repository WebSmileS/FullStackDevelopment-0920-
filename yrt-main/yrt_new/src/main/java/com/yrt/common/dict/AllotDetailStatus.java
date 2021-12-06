package com.yrt.common.dict;

public enum AllotDetailStatus implements BaseIntEnum{
    DRAFT(0, "草稿"),
    CONFIRM(1, "确认"),
    OUTPUT(2, "已出库"),
    COMPLETED(3, "完结"),
    DISCARD(4, "废弃"),
    ;

    private int value;
    private final String text;

    private AllotDetailStatus(int value, String text) {
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
