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
        ...tableColumnConfig.unit
    },
    {
        ...tableColumnConfig.price,
        field: 'unit_price'
    },
    {
        ...tableColumnConfig.count,
        field: 'quantity'
    },
    {
        ...tableColumnConfig.dateYMD,
        title: '采购日期',
        field: 'purchasing_date'
    },
    {
        ...tableColumnConfig.personName,
        title: '采购员',
        field: 'buyer_name'
    },
    {
        ...tableColumnConfig.warehouse
    },
    {
        ...tableColumnConfig.batchNumber
    },
    {
        ...tableColumnConfig.count,
        title: '到货数量',
        field: 'arrival_quantity'
    },
    {
        ...tableColumnConfig.count,
        title: '合格数量',
        field: 'qualified_quantity'
    },
    {
        ...tableColumnConfig.count,
        title: '不合格数量',
        field: 'unqualified_quantity'
    },
    {
        ...tableColumnConfig.count,
        title: '入库数量',
        field: 'grn_quantity'
    }
];
