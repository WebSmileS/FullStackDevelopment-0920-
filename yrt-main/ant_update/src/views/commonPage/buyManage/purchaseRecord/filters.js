import { purchaseRecordStatusMap } from './type';

/**
 * 采购记录状态文本
 * @param {string} vlaue 状态值
 */
export function purchaseRecordStatusTextFilter(value) {
    if (purchaseRecordStatusMap[value]) {
        return purchaseRecordStatusMap[value].text;
    }
    return '';
}
