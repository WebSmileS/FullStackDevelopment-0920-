import tableColumnConfig from '@/config/tableColumnConfig';

export default [
    {
        ...tableColumnConfig.sn
    },
    {
        ...tableColumnConfig.organization,
        title: '相关机构'
    },
    {
        ...tableColumnConfig.product
    },
    {
        ...tableColumnConfig.vendor,
        field: 'product_vendor_name'
    },
    {
        ...tableColumnConfig.specification
    },
    {
        ...tableColumnConfig.batchNumber
    },
    {
        ...tableColumnConfig.personName,
        field: 'buyer_name',
        title: '采购员'
    },
    {
        ...tableColumnConfig.count,
        field: 'quantity',
        title: '数量'
    },
    {
        ...tableColumnConfig.unit
    },
    {
        ...tableColumnConfig.price,
        field: 'unit_price',
        title: '含税单价'
    },
    {
        ...tableColumnConfig.rate,
        field: 'rate',
        cellRender: { name: 'rate' }
    },
    {
        ...tableColumnConfig.dateYMD,
        field: 'produce_date',
        title: '生产日期'
    },
    {
        ...tableColumnConfig.dateYMD,
        field: 'overdue_date',
        title: '失效日期'
    },
    {
        ...tableColumnConfig.mdrf,
        field: 'mdrf_sn',
        title: '注册证号'
    },
    {
        ...tableColumnConfig.healthCareSn
    }
];
