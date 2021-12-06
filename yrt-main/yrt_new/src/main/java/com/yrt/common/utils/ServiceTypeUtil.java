package com.yrt.common.utils;

import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.SystemType;

/**
 * @author ：GuangxiZhong
 * @date ：Created in 2021/8/30 16:11
 * @description：
 * @modified By：
 * @version: 1.0
 */
public class ServiceTypeUtil {

    public static SystemType getFirstType(BillsType billsType){
        switch (billsType){
            case DD:
            case DH:
            case DV:
                return SystemType.DEALER;
            case HD:
            case HH:
            case HV:
                return SystemType.HOSPITAL;
            case VD:
            case VH:
            case VV:
                return SystemType.VENDOR;
        }
        return null;
    }

    public static SystemType getLastType(BillsType billsType){
        switch (billsType){
            case DD:
            case HD:
            case VD:
                return SystemType.DEALER;
            case DH:
            case HH:
            case VH:
                return SystemType.HOSPITAL;
            case HV:
            case VV:
            case DV:
                return SystemType.VENDOR;
        }
        return null;
    }
}
