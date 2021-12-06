export default [
    { title: '序号', alias: undefined, dataIndex: 'index', align: 'center', enabled: true, width: 8 },
    { title: '商品名称', alias: undefined, dataIndex: 'product_name', align: 'center', enabled: true, width: 16 },
    { title: '规格', alias: undefined, dataIndex: 'specification', align: 'center', enabled: true, width: 16 },
    { title: '生产厂家', alias: undefined, dataIndex: 'vendor_name', align: 'center', enabled: true, width: 16 },
    { title: '单位', alias: undefined, dataIndex: 'unit_name', align: 'center', enabled: true, width: 8 },
    {
        title: '单价',
        alias: undefined,
        dataIndex: 'tax_unit_price',
        align: 'right',
        enabled: true,
        width: 15,
        precision: 4
    },
    { title: '数量', alias: undefined, dataIndex: 'quantity', align: 'right', enabled: true, width: 8 },
    {
        title: '总金额（元）',
        alias: undefined,
        dataIndex: 'tax_amount',
        align: 'right',
        enabled: true,
        width: 15,
        precision: 3
    },
    { title: '注册证号', alias: undefined, dataIndex: 'newest_register_sn', align: 'center', enabled: true, width: 16 }
];
