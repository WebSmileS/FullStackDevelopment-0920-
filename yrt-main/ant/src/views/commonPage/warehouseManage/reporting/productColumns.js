import tableColumnConfig from '@/config/tableColumnConfig';

export const productColumns = [
    {
        ...tableColumnConfig.seq
    },
    {
        ...tableColumnConfig.vendor,
        title: '厂商/经销商',
        field: 'product_vendor_name',
        cellRender: { name: 'headerSort' }
    },
    {
        ...tableColumnConfig.product,
        align: 'center',
        title: '产品名称',
        field: 'product_name',
        cellRender: { name: 'headerSort' }
    },
    {
        ...tableColumnConfig.specification,
        cellRender: { name: 'headerSort' }
    },
    {
        ...tableColumnConfig.warehouse
    },
    {
        ...tableColumnConfig.batchNumber
    },
    {
        ...tableColumnConfig.count,
        title: '库存数量',
        field: 'quantity'
    },
    {
        ...tableColumnConfig.unit
    }
];
