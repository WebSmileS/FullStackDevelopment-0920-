import tableColumnConfig from '@/config/tableColumnConfig';

export default [
    {
        ...tableColumnConfig.vendor,
        title: '厂商/经销商',
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
        title: '剩余数量',
        field: 'quantity'
    },
    {
        ...tableColumnConfig.unit,
        field: 'unit'
    },
    {
        ...tableColumnConfig.rate,
        field: 'rate',
        cellRender: { name: 'rate' },
        params: { in: 'decimal' }
    },
    {
        ...tableColumnConfig.price,
        title: '含税单价',
        field: 'unit_price'
    },
    {
        ...tableColumnConfig.price,
        title: '合计金额',
        field: 'money'
    },
    {
        ...tableColumnConfig.premiumPercent,
        field: 'percent',
        cellRender: { name: 'rate' },
        params: { in: 'percent' }
    },
    {
        ...tableColumnConfig.price,
        title: '售价',
        field: 'salesPrice'
    },
    {
        ...tableColumnConfig.price,
        title: '合计售价',
        field: 'totalSalesPrice'
    },
    {
        ...tableColumnConfig.dateYMD,
        title: '生产日期',
        field: 'produce_date'
    },
    {
        ...tableColumnConfig.dateYMD,
        title: '失效日期',
        field: 'overdue_date'
    },
    {
        ...tableColumnConfig.mdrf,
        field: 'mdrf_sn'
    },
    {
        ...tableColumnConfig.healthCareSn
    }
];
