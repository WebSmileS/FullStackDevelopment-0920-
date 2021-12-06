import tableColumnConfig from '@/config/tableColumnConfig';

export default [
    {
        ...tableColumnConfig.vendor,
        field: 'productVendorName'
    },
    {
        ...tableColumnConfig.product,
        field: 'productName'
    },
    {
        ...tableColumnConfig.specification,
        field: 'specificationName'
    },
    {
        ...tableColumnConfig.batchNumber,
        field: 'batchNumber'
    },
    {
        ...tableColumnConfig.count,
        field: 'quantity'
    },
    {
        ...tableColumnConfig.unit,
        field: 'unitId'
    },
    {
        ...tableColumnConfig.type
    },
    {
        ...tableColumnConfig.mdrf,
        field: 'mdrfInnerSn'
    },
    {
        ...tableColumnConfig.healthCareSn,
        field: 'healthCareSn'
    }
].map((column) => Object.freeze(column));
