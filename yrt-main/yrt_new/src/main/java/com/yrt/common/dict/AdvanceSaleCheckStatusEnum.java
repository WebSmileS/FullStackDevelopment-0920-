package com.yrt.common.dict;

/**
 * @author ：GuangxiZhong
 * @date ：Created in 2021/8/11 17:08
 * @description：预售单审核状态枚举
 * @modified By：
 * @version: 1.0
 */
public enum AdvanceSaleCheckStatusEnum implements BaseIntEnum{
    UNCHECKED(0, "未确认"),
    APPROVED(1, "已确认");


    private final int value;
    private final String text;

    AdvanceSaleCheckStatusEnum(int value, String text) {
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
