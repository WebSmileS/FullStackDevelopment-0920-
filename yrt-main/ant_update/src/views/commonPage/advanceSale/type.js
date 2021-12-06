// 预售单状态
export const advanceSaleOrderStatusMap = {
    UNDONE: 'UNDONE',
    COMPLETED: 'COMPLETED'
};
// 预售单状态文本
export const advanceSaleOrderStatusTextMap = {
    [advanceSaleOrderStatusMap.UNDONE]: '未完成',
    [advanceSaleOrderStatusMap.COMPLETED]: '已完成'
};
// 预售单状态颜色
export const advanceSaleOrderStatusColor = {
    [advanceSaleOrderStatusMap.UNDONE]: 'orange',
    [advanceSaleOrderStatusMap.COMPLETED]: 'green'
};

// 预售单审核状态
export const advanceSaleOrderAuditStatusMap = {
    UNCHECKED: 'UNCHECKED',
    APPROVED: 'APPROVED'
};
// 预售单审核状态文本
export const advanceSaleOrderAuditStatusTextMap = {
    [advanceSaleOrderAuditStatusMap.UNCHECKED]: '未审核',
    [advanceSaleOrderAuditStatusMap.APPROVED]: '已审核'
};
// 预售单审核状态颜色
export const advanceSaleOrderAuditStatusColor = {
    [advanceSaleOrderAuditStatusMap.UNCHECKED]: 'orange',
    [advanceSaleOrderAuditStatusMap.APPROVED]: 'green'
};

// 预售单产品类型
export const advanceSaleOrderProductTypeMap = {
    NORMAL: { value: 'NORMAL', text: '常规' },
    GIFT: { value: 'GIFT', text: '赠品' }
};

// 税率
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

// 数量类型
export const countSelecterTypeMap = {
    ODO: 'ODO', // 出库数量
    RETURN: 'RETURN' // 回库数量
};
