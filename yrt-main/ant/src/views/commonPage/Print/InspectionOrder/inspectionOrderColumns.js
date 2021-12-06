export default [
    { title: '行号', alias: undefined, dataIndex: 'index', enabled: true, width: 8 },
    { title: '产品名称', alias: undefined, dataIndex: 'name', align: 'left', enabled: true, width: 16 },
    { title: '规格型号', alias: undefined, dataIndex: 'specification', align: 'left', enabled: true, width: 16 },
    { title: '注册证号', alias: undefined, dataIndex: 'mdrf_sn', align: 'left', enabled: true, width: 16 },
    { title: '生产厂商', alias: undefined, dataIndex: 'vendor_name', align: 'left', enabled: true, width: 16 },
    { title: '包装单位', alias: undefined, dataIndex: 'unit_name', enabled: true, width: 8 },
    { title: '数量', alias: undefined, dataIndex: 'quantity', align: 'right', enabled: true, width: 8 },
    { title: '合格数量', alias: undefined, dataIndex: 'qualified_quantity', align: 'right', enabled: true, width: 8 },
    {
        title: '不合格数量',
        alias: undefined,
        dataIndex: 'unqualified_quantity',
        align: 'right',
        enabled: true,
        width: 8
    },
    { title: '不合格原因', alias: undefined, dataIndex: 'unqualified_reason', align: 'left', enabled: true, width: 16 },
    { title: '批号', alias: undefined, dataIndex: 'batch_number', enabled: true, width: 8 },
    { title: '生产日期', alias: undefined, dataIndex: 'produce_date', enabled: true, width: 18 },
    { title: '失效日期', alias: undefined, dataIndex: 'overdue_date', enabled: true, width: 18 }
];
