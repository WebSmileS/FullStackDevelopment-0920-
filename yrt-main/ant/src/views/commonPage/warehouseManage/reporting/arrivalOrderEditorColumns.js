import tableColumnConfig from '@/config/tableColumnConfig';

// 收货单明细产品列表columns
export default [
    {
        ...tableColumnConfig.vendor,
        field: 'vendor_name'
    },
    {
        ...tableColumnConfig.product
    },
    {
        ...tableColumnConfig.specification
    },
    {
        ...tableColumnConfig.unit,
        field: 'specification_vendor_sn',
        title: '产品编码'
    },
    {
        ...tableColumnConfig.mdrf,
        field: 'mdrf_sn'
    },
    {
        ...tableColumnConfig.count,
        // align: 'center',
        field: 'numbers',
        editRender: { name: '$input', props: { type: 'number' } }
    }
];
