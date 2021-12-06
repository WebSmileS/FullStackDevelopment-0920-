// 采购入库单状态
export const purchaseGrnOrderStatusMap = {
    ALL: { value: -1, text: '全部' },
    FORMAL: { value: 0, text: '正式' },
    APPROVALED: { value: 1, text: '已审核' },
    GRN: { value: 2, text: '已入库' },
    CANCEL: { value: 4, text: '作废' },
    RED: { value: 5, text: '红冲' }
};

export const purchaseGrnOrderProductTypeMap = {
    NORMAL: { value: 0, text: '常规' },
    GIVEAWAY: { value: 1, text: '赠品' }
};

export const purchaseGrnOrderDetailActionTypeMap = {
    DELETE: 0,
    ADD: 1,
    EDIT: 2
};

export const rateTypeMap = {
    0: {
        value: 0,
        text: '0'
    },
    1: {
        value: 0.01,
        text: '1'
    },
    3: {
        value: 0.03,
        text: '3'
    },
    6: {
        value: 0.06,
        text: '6'
    },
    9: {
        value: 0.09,
        text: '9'
    },
    13: {
        value: 0.13,
        text: '13'
    }
};
