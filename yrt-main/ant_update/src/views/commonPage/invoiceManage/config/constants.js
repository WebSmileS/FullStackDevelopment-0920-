import { rateTypeMap } from '@/config/constants';

// 出入库页面类型
export const pageType = {
    'INBOUND': {
        'label': '入库',
        'value': 'INBOUND'
    },
    'OUTBOUND': {
        'label': '出库',
        'value': 'OUTBOUND'
    }
};

// 财务出入库发票的tab选项卡
export const systemInvoiceTabFinance = {
    '0': {
        'label': '待审核',
        'value': 'PENDING'
    },
    '1': {
        'label': '未核销',
        'value': 'NOT_WRITTEN_OFF'
    },
    '2': {
        'label': '已核销',
        'value': 'WRITTEN_OFF'
    },
    '3': {
        'label': '已作废',
        'value': 'HAVE_BEEN_VOIDED'
    }
};

// 付款单|收款单页面类型
export const pagePaymentAttachmentType = {
    'PAYMENTORDER': {
        'label': '付款',
        'value': 'PAYMENTORDER'
    },
    'RECEIVEORDER': {
        'label': '收款',
        'value': 'RECEIVEORDER'
    }
};

// 付款单的tab选项卡
export const paymentTabFinance = {
    '0': {
        'label': '有效付款单',
        'value': 'VALID_PAYMENT_NOTE'
    },
    '1': {
        'label': '作废付款单',
        'value': 'CANCEL_A_BILL_OF_PAYMENT'
    }
};

// 收款单的tab选项卡
export const receiptTabFinance = {
    '0': {
        'label': '有效收款单',
        'value': 'VALID_RECEIPT'
    },
    '1': {
        'label': '作废收款单',
        'value': 'NULLIFY_RECEIPT'
    }
};

// 业务人员的出入库发票 tab选项卡
export const systemInvoiceTab = {
    '0': {
        'label': '未作废发票',
        'value': 'UNVOIDED_INVOICES'
    },
    '1': {
        'label': '作废发票',
        'value': 'VOIDED_INVOICES'
    }
};

// 业务人员的预付款 tab选项卡
export const systemAdvancePaymentTab = {
    0: {
        label: '未作废预付款单',
        value: 'UNVOIDED_PAYMENT'
    },
    1: {
        label: '作废预付款单',
        value: 'VOIDED_PAYMENT'
    }
};

// 发票状态 [出入库单和预付款]
export const invoiceState = {
    'ALL': '全部',
    'DRAFT': '草稿',
    'PENDING': '待审核',
    'NOT_WRITTEN_OFF': '未核销',
    'PARTIAL_WRITE_OFF': '部分核销',
    'WRITTEN_OFF': '已核销'
};

// 付款发票状态 [财务人员]
export const paymentInvoiceState = {
    'ALL': '全部',
    'TO_BE_PAID': '待付款',
    'PAID': '已付款',
    'RECORDED': '已入账'
};

// 收款发票状态 [财务人员]
export const receiveInvoiceState = {
    'ALL': '全部',
    'TO_BE_COLLECTED': '待收款',
    'RECEIVED': '已收款',
    'RECORDED': '已入账'
};

// 发票是否作废
export const cancelState = {
    '0': '未作废',
    '1': '已作废'
};

export const constantAll = 'ALL';
export const format = 'YYYY-MM-DD';

// 发票类型
export const invoiceTypeMap = {
    GENERAL_INVOICE: {
        value: 'GENERAL_INVOICE',
        text: '普通发票'
    },
    VAT_INVOICE: {
        value: 'VAT_INVOICE',
        text: '增值税发票'
    },
    CASH_FLOW_INVOICE: {
        value: 'CASH_FLOW_INVOICE',
        text: '资金往来发票'
    }
};

// 付款方式 | 收款方式
export const paymentMethod = {
    'REMITTANCE': '汇款',
    'CHECK': '支票',
    'OTHERS': '其他'
};

// 税率
export { rateTypeMap };

export function getUseableRateType(rate = 0) {
    if (rateTypeMap[rate]) {
        return rateTypeMap[rate];
    }
    return rateTypeMap[0];
}
export const rateData = Object.keys(rateTypeMap).map((rate) => ({
    title: rateTypeMap[rate].text,
    key: rateTypeMap[rate].value,
    value: rateTypeMap[rate].value
}));

// 发票管理的业务人员使用 的 出入库发票单据类型
export const grnAndOdoInvoiceOrderType = {
    'GRN_INVOICE': {
        label: '入库发票',
        value: 'GRN_INVOICE',
        code: 10
    },
    'ODO_INVOICE': {
        label: '出库发票',
        value: 'ODO_INVOICE',
        code: 11
    },
    'ADVANCE_PAYMENT': {
        label: '预付款',
        value: 'ADVANCE_PAYMENT',
        code: 12
    }
};
