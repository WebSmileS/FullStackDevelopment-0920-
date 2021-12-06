export const BillsType = {
    DealerDealer: 'DD', // 经销商对经销商
    DealerVendor: 'DV', // 经销商对厂商
    DealerHospital: 'DH', // 经销商对医院
    HospitalHospital: 'HH', // 医院对医院
    HospitalDealer: 'HD', // 医院对经销商
    HospitalVendor: 'HV' // 医院对厂商
};

/**
 * 需要获取响应头内容的接口列表
 */
export const NeedResHeaderEndpoints = [
    '/v1/business/finance/grn/export',
    '/v1/business/finance/odo/export',
    '/v1/business/finance/inventory/export',
    '/v1/business/order/purchase/record/export',
    '/v1/business/finance/invoice/export',
    '/v1/business/finance/payment/export',
    '/v1/business/finance/receive/export',
    '/v1/business/finance/advance/export'
];

export const DynamicNeedResHeaderEndpoints = [new RegExp('/v1/business/initial/importDataResult/(.*)')];

export const constants = {
    redDashedOdoOrder: '红冲入库单' // 入库单页面
};

// 图片资源地址
// 格式：pictureAPIHost + '/' + resourceKey
// 缩略图（200*200）格式 pictureAPIHost + '/' + resourceKey + '-200'
export const pictureAPIHost = '/v1/image/get';

// 上传组件指定的excel文件类型
export const ExcelAccept =
    '.xls,.xlsx,application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';

// 效期预警
export const expiryDateWarning = {
    0: {
        label: '资证预警',
        value: 'CERTIFICATIONEARLY'
    },
    1: {
        label: '产品注册证预警',
        value: 'PRODUCTREGISTRATION'
    },
    2: {
        label: '产品效期预警',
        value: 'PRODUCTEXPIRY'
    }
};

// 出入库单据类型
export const grnAndOdoOrderType = {
    /// 原入库单
    PURCHASEGRN: {
        label: '采购入库',
        value: 'PURCHASEGRN'
    },
    /// 原出库单
    PURCHASERETURN: {
        label: '采购退货',
        value: 'PURCHASERETURN'
    },
    /// 原入库单
    ALLOTGRN: {
        label: '调拨入库',
        value: 'ALLOTGRN'
    },
    /// 原出库单
    SALEODO: {
        label: '销售出库',
        value: 'SALEODO'
    },
    /// 原入库单
    SALERETURN: {
        label: '销售退货',
        value: 'SALERETURN'
    },
    /// 原出库单
    ALLOTODO: {
        label: '调拨出库',
        value: 'ALLOTODO'
    },
    /// 原出库单
    GRANTODO: {
        label: '领用出库',
        value: 'GRANTODO'
    },
    /// 原出库单
    GIVEODO: {
        label: '赠送单',
        value: 'GIVEODO'
    }
};

/**
 * 单据类型
 */
export const orderTypeMap = {
    NONE: { key: 'NONE', value: 0 }, //无关联,
    GRANT: { key: 'GRANT', value: 1 }, //发放单,
    SALE_RECORD: { key: 'SALE_RECORD', value: 2 }, //(2, "销售单"),
    ORDER_PO: { key: 'ORDER_PO', value: 3 }, //(3, "采购订单"),
    ORDER_SO: { key: 'ORDER_SO', value: 4 }, //(4, "销售订单"),
    ORTHOPAEDIC: { key: 'ORTHOPAEDIC', value: 5 }, //(5, "骨科计划单"),
    ARRIVAL_RECORD: { key: 'ARRIVAL_RECORD', value: 6 }, //(6, "收货单"),
    ODO: { key: 'ODO', value: 7 }, //(7,"出库单"),
    ALLOT: { key: 'ALLOT', value: 8 }, //(8,"调拨单"),
    GRN: { key: 'GRN', value: 9 }, //(9,"入库单"),
    GRN_INVOICE: { key: 'GRN_INVOICE', value: 10 }, //(10, "入库发票"),
    ODO_INVOICE: { key: 'ODO_INVOICE', value: 11 }, //(11, "出库发票"),
    ADVANCE_PAYMENT: { key: 'ADVANCE_PAYMENT', value: 12 }, //(12,"预付款"),
    PO_RECORDS: { key: 'PO_RECORDS', value: 13 } //(13,"采购记录")
};

export const rateTypeMap = {
    0: {
        value: '0',
        text: '0'
    },
    1: {
        value: '1',
        text: '1'
    },
    3: {
        value: '3',
        text: '3'
    },
    6: {
        value: '6',
        text: '6'
    },
    9: {
        value: '9',
        text: '9'
    },
    13: {
        value: '13',
        text: '13'
    }
};
