import { purchaseGrnOrderStatusMap as statusMap } from './type';

const textMap = (() => {
    const map = {};
    Object.keys(statusMap).forEach((key) => {
        map[statusMap[key].value] = statusMap[key].text;
    });
    return map;
})();

// 采购入库单状态文本
export function purchaseGrnOrderStatusTextFilter(value) {
    if (textMap[value]) {
        return textMap[value];
    }
    return '';
}
