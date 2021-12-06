import { inspectionOrderStatusMap } from './type';

// 收货单状态文本
export function inspectionOrderStatusTextFilter(value) {
    if (inspectionOrderStatusMap[value]) {
        return inspectionOrderStatusMap[value].text;
    }
    return '';
}

// 收货单作废状态文本
export function inspectionOrderCancelTextFilter(value) {
    if (value) {
        return '已作废';
    }
    return '';
}
