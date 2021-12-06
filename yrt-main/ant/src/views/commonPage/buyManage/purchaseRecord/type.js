// 采购记录状态
export const purchaseRecordStatusMap = {
    TO_BE_VERIFY: { value: 'TO_BE_VERIFY', text: '未审核' },
    TO_BE_CONFIRM: { value: 'TO_BE_CONFIRM', text: '待确认' },
    EXECUTING: { value: 'EXECUTING', text: '执行中' },
    FINISHED: { value: 'FINISHED', text: '完成' },
    CANCEL: { value: 'CANCEL', text: '终止' }
};

export const purchaseRecordSearchStatusMap = {
    ALL: { value: 0, text: '全部' },
    EXECUTING: { value: 1, text: '执行中' },
    FINISHED: { value: 2, text: '完成' }
};
