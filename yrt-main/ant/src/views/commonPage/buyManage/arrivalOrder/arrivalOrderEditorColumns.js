import tableColumnConfig from '@/config/tableColumnConfig';

// 收货单明细产品列表columns
export default [
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
        ...tableColumnConfig.unit,
        field: 'unit_inner_sn',
        editRender: { name: 'unitSelecter' }
    },
    {
        ...tableColumnConfig.count,
        title: '订单数量',
        field: 'order_quantity',
        editRender: { name: '$input', props: { type: 'float', digits: 2 } }
    },
    {
        ...tableColumnConfig.batchNumber,
        editRender: { name: '$input', autoselect: true, props: { type: 'text' } }
    },
    {
        ...tableColumnConfig.count,
        title: '收货数量',
        field: 'arrival_quantity',
        editRender: { name: '$input', props: { type: 'float', digits: 2 } }
    },
    {
        ...tableColumnConfig.barcode,
        title: '扫码（PI）',
        editRender: { name: 'barcodeInput', autoselect: true }
    }
];
