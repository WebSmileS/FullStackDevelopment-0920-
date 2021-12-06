import tableColumnConfig from '@/config/tableColumnConfig';

export default [
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
