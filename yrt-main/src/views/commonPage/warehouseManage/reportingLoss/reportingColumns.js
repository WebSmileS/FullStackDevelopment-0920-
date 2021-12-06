// 报损、报溢tbale column
import tableColumnConfig from '@/config/tableColumnConfig';

export const lossReportingColumn = [
    {
        ...tableColumnConfig.seq
    },
    {
        ...tableColumnConfig.product,
        align: 'center',
        title: '产品名称',
        field: 'product_name',
        cellRender: { name: 'headerSort' }
    },
    {
        ...tableColumnConfig.vendor,
        title: '厂商/经销商',
        field: 'product_vendor_name',
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
        width: 100,
        title: '数量',
        field: 'quantity'
    },
    {
        ...tableColumnConfig.unit
    },
    {
        ...tableColumnConfig.price,
        width: 100,
        title: '成本价(元)',
        field: 'unit_price'
    },
    {
        ...tableColumnConfig.price,
        width: 110,
        title: '库存金额',
        field: 'storage_price'
    }
];
