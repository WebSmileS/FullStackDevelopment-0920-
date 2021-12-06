import tableColumnConfig from '@/config/tableColumnConfig';

const mdrfParser = (_, { row }) => row.mdrf_sn;

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
        ...tableColumnConfig.unit
    },
    {
        ...tableColumnConfig.count,
        title: '收货数量',
        field: 'arrival_quantity'
    },
    {
        ...tableColumnConfig.batchNumber,
        editRender: { name: '$input', autoselect: true, props: { type: 'text' } }
    },
    {
        ...tableColumnConfig.count,
        title: '合格数量',
        field: 'qualified_quantity',
        editRender: { name: '$input', props: { type: 'number', min: 0 } }
    },
    {
        ...tableColumnConfig.count,
        title: '不合格数量',
        field: 'unqualified_quantity',
        editRender: { name: '$input', props: { type: 'number', min: 0 } }
    },
    {
        ...tableColumnConfig.longText,
        title: '不合格原因及处理意见',
        field: 'unqualified_reason',
        editRender: { name: '$input', props: { type: 'text' } }
    },
    {
        ...tableColumnConfig.mdrf,
        field: 'mdrf_inner_sn',
        editRender: { name: 'customSelecter' },
        params: {
            customSelecterChangeMap: {
                value: ['mdrf_inner_sn'],
                label: ['mdrf_sn']
            }
        },
        cellRender: { name: 'valueParser', parser: mdrfParser }
    },
    {
        ...tableColumnConfig.dateYMD,
        title: '生产日期',
        field: 'produce_date',
        editRender: { name: '$input', props: { type: 'date' } }
    },
    {
        ...tableColumnConfig.dateYMD,
        title: '失效日期',
        field: 'overdue_date',
        editRender: { name: '$input', props: { type: 'date' } }
    },
    {
        ...tableColumnConfig.barcode,
        title: '扫码（PI）',
        editRender: { name: 'barcodeInput', autoselect: true }
    }
];
