package com.yrt.common.dict;

/**
 * @author ：GuangxiZhong
 * @date ：Created in 2021/8/11 17:04
 * @description：机构类型
 * @modified By：
 * @version: 1.0
 */
public enum OrgTypeEnum implements BaseIntEnum{

    V(0,"厂商"),
    D(1, "经销商"),
    H(2, "医院");


    private final int value;
    private final String text;

    private OrgTypeEnum(int value, String text) {
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
