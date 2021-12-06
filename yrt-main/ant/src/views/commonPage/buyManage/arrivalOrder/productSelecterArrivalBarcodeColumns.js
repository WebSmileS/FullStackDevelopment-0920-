import tableColumnConfig from '@/config/tableColumnConfig';

export default [
    {
        ...tableColumnConfig.sn
    },
    {
        ...tableColumnConfig.organization,
        title: '供货单位'
    },
    {
        ...tableColumnConfig.product
    },
    {
        ...tableColumnConfig.specification
    },
    {
        ...tableColumnConfig.vendor,
        field: 'product_vendor_name'
    },
    {
        ...tableColumnConfig.batchNumber
    },
    {
        ...tableColumnConfig.count,
        title: '订单数量',
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
