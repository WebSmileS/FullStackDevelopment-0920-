import XEUtils from 'xe-utils';

// 【出库】【发放单】上方表格列
export const MainTableColumnsOdoGrant = [
    {
        field: 'sn',
        title: '单据编号',
        minWidth: '250px'
    },
    {
        field: 'department_name',
        title: '部门',
        dataIndex: 'department_name',
        key: 'department_name',
        minWidth: '100px',
        align: 'center'
    },
    {
        field: 'warehouse_name',
        title: '仓库',
        dataIndex: 'warehouse_name',
        key: 'warehouse_name',
        minWidth: '250px',
        align: 'center'
    },
    {
        field: 'approver_name',
        title: '发放人',
        dataIndex: 'approver_name',
        key: 'approver_name',
        minWidth: '200px',
        align: 'center'
    },
    {
        field: 'grant_time',
        title: '发放时间',
        dataIndex: 'grant_time',
        key: 'grant_time',
        minWidth: '200px',
        align: 'center'
    }
];
// 【出库】【销售单】上方表格列
export const MainTableColumnsOdoSale = [
    {
        field: 'sn',
        title: '单据编号',
        dataIndex: 'sn',
        key: 'sn',
        minWidth: '350px',
        align: 'center'
    },
    {
        field: 'organization_name',
        title: '单位名称',
        dataIndex: 'organization_name',
        key: 'organization_name',
        minWidth: '200px',
        align: 'center'
    },
    {
        field: 'seller_name',
        title: '销售员',
        dataIndex: 'seller_name',
        key: 'seller_name',
        minWidth: '200px',
        align: 'center'
    },
    {
        field: 'department_name',
        title: '部门',
        dataIndex: 'department_name',
        key: 'department_name',
        minWidth: '200px',
        align: 'center'
    }
];
// 【入库】【采购订单】上方表格列
export const MainTableColumnsGrnBuy = [
    {
        field: 'organization_name',
        title: '单位名称',
        dataIndex: 'organization_name',
        key: 'organization_name',
        minWidth: '200px',
        align: 'center'
    },
    {
        field: 'sn',
        title: '单据编号',
        dataIndex: 'sn',
        key: 'sn',
        minWidth: '150px',
        align: 'center'
    },
    {
        field: 'warehouse_name',
        title: '仓库',
        dataIndex: 'warehouse_name',
        key: 'warehouse_name',
        minWidth: '200px',
        align: 'center'
    },
    {
        field: 'handler_name',
        title: '经手人',
        dataIndex: 'handler_name',
        key: 'handler_name',
        minWidth: '200px',
        align: 'center'
    },
    {
        field: 'department_name',
        title: '部门',
        dataIndex: 'department_name',
        key: 'department_name',
        minWidth: '200px',
        align: 'center'
    }
];
// 【调拨】上方表格列
export const MainTableColumnsAllot = [
    {
        field: 'sn',
        title: '单据编号',
        dataIndex: 'sn',
        key: 'sn',
        minWidth: '150px',
        align: 'center'
    },
    {
        field: 'source_warehouse_name',
        title: '发出仓库',
        dataIndex: 'source_warehouse_name',
        key: 'source_warehouse_name',
        minWidth: '200px',
        align: 'center'
    },
    {
        field: 'target_warehouse_name',
        title: '目标仓库',
        dataIndex: 'target_warehouse_name',
        key: 'target_warehouse_name',
        minWidth: '200px',
        align: 'center'
    },
    {
        field: 'expect_date',
        title: '期望完成调拨日期',
        dataIndex: 'expect_date',
        key: 'expect_date',
        minWidth: '200px',
        align: 'center'
    }
];
// 【出库】【发放单】 下方表格列
export const DetailTableColumnsOdoGrant = [
    {
        title: '',
        dataIndex: '',
        minWidth: '50',
        type: 'checkbox',
        align: 'center'
    },
    {
        field: 'product_name',
        title: '产品',
        dataIndex: 'product_name',
        key: 'product_name',
        minWidth: '200px',
        align: 'center'
    },
    {
        field: 'specification',
        title: '规格型号',
        dataIndex: 'specification',
        key: 'specification',
        minWidth: '150px',
        align: 'center'
    },
    {
        field: 'vendor_name',
        title: '厂商/经销商',
        dataIndex: 'vendor_name',
        key: 'vendor_name',
        minWidth: '200px',
        align: 'center'
    },
    {
        field: 'batch_number',
        title: '批号',
        dataIndex: 'batch_number',
        key: 'batch_number',
        minWidth: '150px',
        align: 'center'
    },
    {
        field: 'grant_quantity',
        title: '批准发放量',
        dataIndex: 'grant_quantity',
        key: 'grant_quantity',
        minWidth: '150px',
        align: 'center'
    },
    {
        field: 'unit_name',
        title: '包装单位',
        dataIndex: 'unit_name',
        key: 'unit_name',
        minWidth: '100px',
        align: 'center'
    },
    {
        field: 'status_name',
        title: '状态',
        dataIndex: 'status_name',
        key: 'status_name',
        minWidth: '100px',
        align: 'center'
    },
    {
        field: 'produce_date',
        title: '生产日期',
        dataIndex: 'produce_date',
        key: 'produce_date',
        minWidth: '150px',
        align: 'center'
    },
    {
        field: 'overdue_date',
        title: '失效日期',
        dataIndex: 'overdue_date',
        key: 'overdue_date',
        minWidth: '150px',
        align: 'center'
    }
];
// 【出库】【销售单】 下方表格列
export const DetailTableColumnsOdoSale = [
    {
        title: '',
        dataIndex: '',
        minWidth: '5%',
        type: 'checkbox',
        align: 'center'
    },
    {
        field: 'product_name',
        title: '产品',
        dataIndex: 'product_name',
        key: 'product_name',
        minWidth: '200px',
        align: 'center'
    },
    {
        field: 'specification',
        title: '规格型号',
        dataIndex: 'specification',
        key: 'specification',
        minWidth: '100px',
        align: 'center'
    },
    {
        field: 'product_vendor_name',
        title: '厂商/经销商',
        dataIndex: 'product_vendor_name',
        key: 'product_vendor_name',
        minWidth: '200px',
        align: 'center'
    },
    {
        field: 'batch_number',
        title: '批号',
        dataIndex: 'batch_number',
        key: 'batch_number',
        minWidth: '100px',
        align: 'center'
    },
    {
        field: 'quantity',
        title: '数量',
        dataIndex: 'quantity',
        key: 'quantity',
        minWidth: '100px',
        align: 'center'
    },
    {
        field: 'unit_name',
        title: '包装单位',
        dataIndex: 'unit_name',
        key: 'unit_name',
        minWidth: '100px',
        align: 'center'
    },
    {
        field: 'outOfStock_quantity',
        title: '已出库数量',
        dataIndex: 'outOfStock_quantity',
        key: 'outOfStock_quantity',
        minWidth: '150px',
        align: 'center'
    },
    {
        field: 'toBeOutOfStock_quantity',
        title: '待出库数量',
        dataIndex: 'toBeOutOfStock_quantity',
        key: 'toBeOutOfStock_quantity',
        minWidth: '150px',
        align: 'center'
    }
];
// 【停用】【收货】经销商端-调单上边表格的Columns
export const MainTableColumnsReceiving = [
    {
        field: 'organization_name',
        title: '单位名称',
        dataIndex: 'organization_name',
        key: 'organization_name',
        minWidth: '200px',
        align: 'center'
    },
    {
        field: 'sl_sn',
        title: '单据编号',
        dataIndex: 'sl_sn',
        key: 'sl_sn',
        minWidth: '150px',
        align: 'center'
    },
    {
        field: 'seller_name',
        title: '销售员',
        dataIndex: 'seller_name',
        key: 'seller_name',
        minWidth: '200px',
        align: 'center'
    },
    {
        field: 'seller_linkman',
        title: '销售方联系人',
        dataIndex: 'seller_linkman',
        key: 'seller_linkman',
        minWidth: '200px',
        align: 'center'
    },
    {
        field: 'seller_phone',
        title: '电话',
        dataIndex: 'seller_phone',
        key: 'seller_phone',
        minWidth: '200px',
        align: 'center'
    }
];
// 【收货】经销商端-调单下边表格的Columns
// 【入库】【出库单】
// 【入库】【采购订单】
export const DetailTableColumnsGrnCommon = [
    {
        title: '',
        dataIndex: '',
        minWidth: '5%',
        type: 'checkbox',
        align: 'center'
    },
    {
        field: 'product_name',
        title: '产品',
        dataIndex: 'product_name',
        key: 'product_name',
        minWidth: '200px',
        align: 'center'
    },
    {
        field: 'specification',
        title: '规格型号',
        dataIndex: 'specification',
        key: 'specification',
        minWidth: '100px',
        align: 'center'
    },
    {
        field: 'product_vendor_name',
        title: '厂商/经销商',
        dataIndex: 'product_vendor_name',
        key: 'product_vendor_name',
        minWidth: '200px',
        align: 'center'
    },
    {
        field: 'batch_number',
        title: '批号',
        dataIndex: 'batch_number',
        key: 'batch_number',
        minWidth: '100px',
        align: 'center'
    },
    {
        field: 'quantity',
        title: '数量',
        dataIndex: 'quantity',
        key: 'quantity',
        minWidth: '80px',
        align: 'center'
    },
    {
        field: 'unit',
        title: '包装单位',
        dataIndex: 'unit_name',
        key: 'unit',
        minWidth: '80px',
        align: 'center'
    },
    {
        field: 'unit_price',
        title: '含税单价',
        dataIndex: 'unit_price',
        key: 'unit_price',
        minWidth: '80px',
        align: 'center',
        slots: {
            default: ({ row }) => {
                return [XEUtils.commafy(row.unit_price, { fixed: 4 })];
            }
        }
    },
    {
        field: 'price',
        title: '含税金额',
        dataIndex: 'price',
        key: 'price',
        minWidth: '80px',
        align: 'center',
        slots: {
            default: ({ row }) => {
                return [XEUtils.commafy(row.price, { fixed: 2 })];
            }
        }
    }
];
// 【入库】【调拨】下方表格列
export const DetailTableColumnsGrnAllot = [
    {
        title: '',
        dataIndex: '',
        minWidth: '5%',
        type: 'checkbox',
        align: 'center'
    },
    {
        field: 'product_name',
        title: '产品',
        dataIndex: 'product_name',
        key: 'product_name',
        minWidth: '200px',
        align: 'center'
    },
    {
        field: 'specification',
        title: '规格型号',
        dataIndex: 'specification',
        key: 'specification',
        minWidth: '100px',
        align: 'center'
    },
    {
        field: 'product_vendor_name',
        title: '厂商/经销商',
        dataIndex: 'product_vendor_name',
        key: 'product_vendor_name',
        minWidth: '200px',
        align: 'center'
    },
    {
        field: 'batch_number',
        title: '批号',
        dataIndex: 'batch_number',
        key: 'batch_number',
        minWidth: '100px',
        align: 'center'
    },
    {
        field: 'unit',
        title: '包装单位',
        dataIndex: 'unit_name',
        key: 'unit',
        minWidth: '80px',
        align: 'center'
    },
    {
        field: 'inOfStock_quantity',
        title: '已入库数量',
        dataIndex: 'inOfStock_quantity',
        key: 'inOfStock_quantity',
        minWidth: '80px',
        align: 'center'
    },
    {
        field: 'toBeInOfStock_quantity',
        title: '待入库数量',
        dataIndex: 'toBeInOfStock_quantity',
        key: 'toBeInOfStock_quantity',
        minWidth: '80px',
        align: 'center'
    },
    {
        field: 'allot_quantity',
        title: '调拨数量',
        dataIndex: 'allot_quantity',
        key: 'allot_quantity',
        minWidth: '80px',
        align: 'center'
    },
    {
        field: 'unit_price',
        title: '含税单价',
        dataIndex: 'unit_price',
        key: 'unit_price',
        minWidth: '80px',
        align: 'center',
        slots: {
            default: ({ row }) => {
                return [XEUtils.commafy(row.unit_price, { fixed: 4 })];
            }
        }
    },
    {
        field: 'tvStatus',
        title: '入库状态',
        dataIndex: 'tvStatus',
        key: 'tvStatus',
        minWidth: '80px',
        align: 'center'
    }
];

// 【出库】【调拨】下方表格列
export const DetailTableColumnsOdoAllot = [
    {
        title: '',
        dataIndex: '',
        minWidth: '5%',
        type: 'checkbox',
        align: 'center'
    },
    {
        field: 'product_name',
        title: '产品',
        dataIndex: 'product_name',
        key: 'product_name',
        minWidth: '200px',
        align: 'center'
    },
    {
        field: 'specification',
        title: '规格型号',
        dataIndex: 'specification',
        key: 'specification',
        minWidth: '100px',
        align: 'center'
    },
    {
        field: 'product_vendor_name',
        title: '厂商/经销商',
        dataIndex: 'product_vendor_name',
        key: 'product_vendor_name',
        minWidth: '200px',
        align: 'center'
    },
    {
        field: 'batch_number',
        title: '批号',
        dataIndex: 'batch_number',
        key: 'batch_number',
        minWidth: '100px',
        align: 'center'
    },
    {
        field: 'unit',
        title: '包装单位',
        dataIndex: 'unit_name',
        key: 'unit',
        minWidth: '80px',
        align: 'center'
    },
    {
        field: 'outOfStock_quantity',
        title: '已出库数量',
        dataIndex: 'outOfStock_quantity',
        key: 'outOfStock_quantity',
        minWidth: '80px',
        align: 'center'
    },
    {
        field: 'toBeOutOfStock_quantity',
        title: '待出库数量',
        dataIndex: 'toBeOutOfStock_quantity',
        key: 'toBeOutOfStock_quantity',
        minWidth: '80px',
        align: 'center'
    },
    {
        field: 'allot_quantity',
        title: '调拨数量',
        dataIndex: 'allot_quantity',
        key: 'allot_quantity',
        minWidth: '80px',
        align: 'center'
    },
    {
        field: 'unit_price',
        title: '含税单价',
        dataIndex: 'unit_price',
        key: 'unit_price',
        minWidth: '80px',
        align: 'center',
        slots: {
            default: ({ row }) => {
                return [XEUtils.commafy(row.unit_price, { fixed: 4 })];
            }
        }
    },
    {
        field: 'tvStatus',
        title: '出库状态',
        dataIndex: 'tvStatus',
        key: 'tvStatus',
        minWidth: '80px',
        align: 'center'
    }
];
