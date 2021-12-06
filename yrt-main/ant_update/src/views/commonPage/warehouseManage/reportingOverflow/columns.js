import tableColumnConfig from '@/config/tableColumnConfig';

// 产品列表column
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

// 可用产品列表tbale column
export const productSelecterAdvanceSaleColumns = [
    {
        ...tableColumnConfig.vendor
    },
    {
        ...tableColumnConfig.product
    },
    {
        ...tableColumnConfig.specification
    },
    {
        ...tableColumnConfig.warehouse
    },
    {
        ...tableColumnConfig.count,
        field: 'quantity'
    },
    {
        ...tableColumnConfig.unit,
        field: 'min_unit'
    },
    {
        ...tableColumnConfig.mdrf,
        field: 'mdrf_sn'
    }
];

// 新增报损单列表column
export const reportingOverflowEditorColumns = [
    {
        ...tableColumnConfig.vendor,
        field: 'vendor_name'
    },
    {
        ...tableColumnConfig.product,
        field: 'product_name'
    },
    {
        ...tableColumnConfig.specification,
        field: 'specification'
    },
    {
        ...tableColumnConfig.count,
        title: '报溢数量',
        field: 'overflownumber',
        editRender: { name: '$input', props: { type: 'number' } }
    },
    {
        ...tableColumnConfig.unit,
        field: 'min_unit'
    },
    {
        ...tableColumnConfig.mdrf,
        field: 'mdrf_sn'
    }
];