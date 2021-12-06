export default [
    {
        title: '产品',
        dataIndex: 'product_name',
        width: '10%'
    },
    {
        title: '规格型号',
        dataIndex: 'specification',
        width: '5%'
    },
    {
        title: '厂商名称',
        dataIndex: 'product_vendor_name',
        width: '10%'
    },
    {
        title: '包装单位',
        dataIndex: 'unit_inner_sn',
        width: '5%',
        editRender: { name: 'unitSelecter' }
    },
    {
        title: '批号',
        dataIndex: 'batch_number',
        width: '5%',
        editRender: { name: '$input', props: { type: 'text' } }
    },
    {
        title: '数量',
        dataIndex: 'quantity',
        width: '5%',
        editRender: { name: '$input', props: { type: 'float', digits: 2, min: 0.01 } }
    },
    {
        title: '单价',
        dataIndex: 'unit_price',
        width: '5%',
        editRender: { name: '$input', props: { type: 'float', digits: 4, min: 0.0001 } }
    },
    {
        title: '生产日期',
        dataIndex: 'produce_date',
        width: '5%',
        editRender: { name: '$input', props: { type: 'datetime' } }
    },
    {
        title: '失效日期',
        dataIndex: 'overdue_date',
        width: '5%',
        editRender: { name: '$input', props: { type: 'datetime' } }
    }
];
