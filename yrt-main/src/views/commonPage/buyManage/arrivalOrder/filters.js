import { arrivalOrderStatusMap } from './type';

// 收货单状态文本
export function arrivalOrderStatusTextFilter(value) {
    if (arrivalOrderStatusMap[value]) {
        return arrivalOrderStatusMap[value].text;
    }
    return '';
}

// 收货单作废状态文本
export function arrivalOrderCancelTextFilter(value) {
    if (value) {
        return '已作废';
    }
    return '';
}
