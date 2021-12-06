import tableColumnConfig from '@/config/tableColumnConfig';

export const level0Columns = [
    {
        ...tableColumnConfig.vendor,
        title: '厂商/经销商',
        field: 'product_vendor_name',
        cellRender: { name: 'headerSort' }
    },
    {
        ...tableColumnConfig.product,
        cellRender: { name: 'headerSort' }
    },
    {
        ...tableColumnConfig.specification,
        cellRender: { name: 'headerSort' }
    },
    {
        ...tableColumnConfig.count,
        title: '数量',
        field: 'quantity'
    },
    {
        ...tableColumnConfig.unit,
        field: 'unit'
    },
    {
        ...tableColumnConfig.price,
        title: '成本均价',
        field: 'unit_price'
    },
    {
        ...tableColumnConfig.price,
        title: '库存金额',
        field: 'storage_price'
    }
];

export const level1Columns = [
    {
        ...tableColumnConfig.product,
        title: '厂商/经销商',
        field: 'product_vendor_name',
        cellRender: { name: 'headerSort' }
    },
    {
        ...tableColumnConfig.product,
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
        ...tableColumnConfig.count,
        title: '数量',
        field: 'quantity'
    },
    {
        ...tableColumnConfig.unit,
        field: 'unit',
        key: 'unit'
    },
    {
        ...tableColumnConfig.price,
        title: '成本均价',
        field: 'unit_price'
    },
    {
        ...tableColumnConfig.price,
        title: '库存金额',
        field: 'storage_price'
    }
];

export const level2Columns = [
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
        ...tableColumnConfig.dateYMD,
        title: '生产日期',
        field: 'produce_date'
    },
    {
        ...tableColumnConfig.dateYMD,
        title: '失效日期',
        field: 'overdue_date'
    },
    {
        ...tableColumnConfig.count,
        title: '数量',
        field: 'quantity'
    },
    {
        ...tableColumnConfig.unit
    },
    {
        ...tableColumnConfig.price,
        title: '成本均价',
        field: 'unit_price'
    },
    {
        ...tableColumnConfig.price,
        width: 110,
        title: '库存金额',
        field: 'storage_price'
    }
];

export const modelingColumns = [
    {
        ...tableColumnConfig.vendor,
        field: 'name2'
    },
    {
        ...tableColumnConfig.product,
        field: 'name'
    },
    {
        ...tableColumnConfig.specification,
        field: 'name1'
    },
    {
        ...tableColumnConfig.batchNumber
    },
    {
        ...tableColumnConfig.count,
        field: 'quantity'
    },
    {
        ...tableColumnConfig.unit
    },
    {
        ...tableColumnConfig.dateYMD,
        title: '生产日期',
        field: 'name6'
    },
    {
        ...tableColumnConfig.dateYMD,
        title: '失效日期',
        field: 'name7'
    },
    {
        ...tableColumnConfig.warehouse,
        title: '存储位置',
        field: 'name8'
    },
    {
        ...tableColumnConfig.mdrf,
        field: 'mdrf_sn'
    },
    {
        ...tableColumnConfig.status,
        title: '状态',
        field: 'status',
        scopedSlots: { customRender: 'status_text' }
    },
    {
        ...tableColumnConfig.dateYMD,
        title: '入库时间',
        field: 'name10'
    }
];
