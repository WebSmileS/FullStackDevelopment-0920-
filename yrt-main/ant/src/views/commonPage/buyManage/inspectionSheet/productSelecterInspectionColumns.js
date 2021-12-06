import tableColumnConfig from '@/config/tableColumnConfig';

// 收货单产品选择列表columns
export default [
    {
        ...tableColumnConfig.sn
    },
    {
        ...tableColumnConfig.organization,
        title: '供货单位'
    },
    {
        ...tableColumnConfig.vendor,
        field: 'product_vendor_name'
    },
    {
        ...tableColumnConfig.product
    },
    {
        ...tableColumnConfig.specification
    },
    {
        ...tableColumnConfig.batchNumber
    },
    {
        ...tableColumnConfig.count,
        title: '收货数量',
        field: 'order_quantity'
    },
    {
        ...tableColumnConfig.unit
    },
    {
        ...tableColumnConfig.warehouse
    },
    {
        ...tableColumnConfig.dateYMD,
        title: '采购日期',
        field: 'purchasing_date'
    }
];
