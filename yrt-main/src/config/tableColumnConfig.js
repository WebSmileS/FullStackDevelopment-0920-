const tableColumnConfig = {
    seq: {
        align: 'center',
        title: 'NO.',
        width: 48,
        resizable: true,
        field: 'seq',
        type: 'seq'
    },
    select: {
        align: 'center',
        width: 48,
        resizable: false,
        field: 'select'
    },
    operation: {
        align: 'center',
        title: '操作',
        width: 90,
        field: 'operation',
        fixed: 'right'
    },
    sn: {
        align: 'center',
        title: '单据编号',
        width: 160,
        field: 'sn'
    },
    organization: {
        align: 'center',
        title: '机构名称',
        width: 200,
        field: 'organization_name'
    },
    personName: {
        align: 'center',
        title: '人名',
        width: 80,
        field: 'name'
    },
    warehouse: {
        align: 'center',
        title: '仓库',
        width: 100,
        field: 'warehouse_name'
    },
    department: {
        align: 'center',
        title: '部门',
        width: 100,
        field: 'department'
    },
    vendor: {
        align: 'center',
        title: '厂商名称',
        width: 200,
        field: 'vendor_name'
    },
    product: {
        align: 'center',
        title: '产品名称',
        width: 200,
        field: 'product_name'
    },
    specification: {
        align: 'center',
        title: '规格型号',
        width: 160,
        field: 'specification'
    },
    unit: {
        align: 'center',
        title: '包装单位',
        width: 80,
        field: 'unit'
    },
    count: {
        align: 'right',
        'header-align': 'center',
        title: '数量',
        width: 80,
        field: 'count'
    },
    batchNumber: {
        align: 'center',
        title: '批号',
        width: 120,
        field: 'batch_number'
    },
    mdrf: {
        align: 'center',
        title: '注册证',
        width: 120,
        field: 'mdrf'
    },
    healthCareSn: {
        align: 'center',
        title: '医保编号',
        width: 120,
        field: 'health_care_sn'
    },
    price: {
        align: 'right',
        'header-align': 'center',
        title: '价格',
        width: 80,
        field: 'price'
    },
    rate: {
        align: 'right',
        'header-align': 'center',
        title: '税率',
        width: 60,
        field: 'rate'
    },
    premiumPercent: {
        align: 'right',
        'header-align': 'center',
        title: '溢价比例',
        width: 65,
        field: 'premium_percent'
    },
    type: {
        align: 'center',
        title: '类别',
        width: 100,
        field: 'type'
    },
    barcode: {
        align: 'center',
        title: '条码',
        width: 120,
        field: 'barcode'
    },
    tag: {
        align: 'center',
        title: '标签',
        width: 160
    },
    dateYMD: {
        align: 'center',
        title: '日期',
        width: 100
    },
    dateYMDHMS: {
        align: 'center',
        title: '时间',
        width: 130
    },
    status: {
        align: 'center',
        title: '状态',
        width: 90,
        field: 'status'
    },
    yesOrNo: {
        align: 'center',
        title: '是否',
        width: 80,
        field: 'is'
    },
    description: {
        align: 'center',
        title: '备注',
        width: 160,
        field: 'description'
    },
    longText: {
        align: 'center',
        title: '文本',
        width: 160,
        field: 'longText'
    }
};

Object.freeze(tableColumnConfig);
Object.keys(tableColumnConfig).forEach((key) => {
    Object.freeze(tableColumnConfig[key]);
});

export default tableColumnConfig;
