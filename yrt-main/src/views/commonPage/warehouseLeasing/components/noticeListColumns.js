/* 入库和出库通知列表的columns */
export default [
    {
        title: '租户',
        dataIndex: 'dealer_name',
        key: 'dealer_name',
        width: '10%',
        align: 'center'
    },
    {
        title: '销售公司',
        dataIndex: 'sales_company_name',
        key: 'sales_company_name',
        width: '10%',
        align: 'center'
    },
    {
        title: '采购公司',
        dataIndex: 'purchasing_company_name',
        key: 'purchasing_company_name',
        width: '10%',
        align: 'center'
    },
    {
        title: '仓库',
        dataIndex: 'warehouse_name',
        key: 'warehouse_name',
        width: '10%',
        align: 'center'
    },
    {
        title: '厂商',
        dataIndex: 'product_vendor_name',
        key: 'vendor_name',
        width: '10%',
        align: 'center'
    },
    {
        title: '产品',
        dataIndex: 'product_name',
        key: 'product_name',
        width: '10%',
        align: 'center'
    },
    {
        title: '规格型号',
        dataIndex: 'specification',
        key: 'specification',
        width: '7%',
        align: 'center'
    },
    {
        title: '批号',
        dataIndex: 'batch_number',
        key: 'batch_number',
        width: '7%',
        align: 'center'
    },
    {
        title: '数量',
        dataIndex: 'quantity',
        key: 'quantity',
        width: '7%',
        align: 'center'
    },
    {
        title: '单位',
        dataIndex: 'unit',
        key: 'unit',
        width: '7%',
        align: 'center',
        showOverflow: 'tooltip'
    },
    {
        title: '单价',
        dataIndex: 'unit_price',
        key: 'unit_price',
        width: '7%',
        align: 'center',
        showOverflow: 'tooltip'
    }
];
