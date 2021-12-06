// 格式化数据的minxin
// [value: 源数据]

import {
    cancelState,
    invoiceTypeMap,
    invoiceState,
    paymentMethod,
    paymentInvoiceState,
    receiveInvoiceState
} from '@/views/commonPage/invoiceManage/config/constants';
export const filterFormat = {
    filters: {
        // 税率 [乘以100 界面上的显示]
        rateMulFilter: (value) => {
            return `${value ? parseFloat(value) * 100 : value} %`;
        },
        // 发票是否作废
        cancelStateFilter: (value) => {
            return cancelState[value];
        },
        // 出入库的发票类型
        invoiceTypeFilter: (value) => {
            if (!value) return value;
            return invoiceTypeMap[value].text;
        },
        // 出入库的发票状态 | 预付款的状态
        invoiceStateFilter: (value) => {
            if (!value) return value;
            return invoiceState[value];
        },
        // 预付款的付款方式
        paymentAdvanceMethodFilter: (value) => {
            if (!value) return value;
            return paymentMethod[value];
        },
        // 财务人员付款单的状态
        paymentInvoiceStateFilter: (value) => {
            if (!value) return value;
            return paymentInvoiceState[value];
        },
        // 财务人员收款单的状态
        receiveInvoiceStateFilter: (value) => {
            if (!value) return value;
            return receiveInvoiceState[value];
        }
    }
};
