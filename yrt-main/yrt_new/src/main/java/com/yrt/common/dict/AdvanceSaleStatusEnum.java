package com.yrt.common.dict;

/**
 * @author ：GuangxiZhong
 * @date ：Created in 2021/8/11 17:12
 * @description：预售单状态枚举
 * @modified By：
 * @version: 1.0
 */
public enum  AdvanceSaleStatusEnum implements BaseIntEnum{
    UNDONE(0, "未完成"),
    COMPLETED(1, "已完成");


    private final int value;
    private final String text;

    AdvanceSaleStatusEnum(int value, String text) {
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
