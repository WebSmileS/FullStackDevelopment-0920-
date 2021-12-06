import tableColumnConfig from '@/config/tableColumnConfig';

// 收货单列表columns
export default [
    {
        ...tableColumnConfig.sn
    },
    {
        ...tableColumnConfig.organization,
        title: '供货单位'
    },
    {
        ...tableColumnConfig.warehouse
    },
    {
        ...tableColumnConfig.personName,
        title: '送货员',
        field: 'deliverymen_name'
    },
    {
        ...tableColumnConfig.personName,
        title: '创建人',
        field: 'created_by'
    },
    {
        ...tableColumnConfig.dateYMD,
        title: '创建时间',
        field: 'created_time'
    },
    {
        ...tableColumnConfig.personName,
        title: '收货人',
        field: 'arrival_by'
    },
    {
        ...tableColumnConfig.dateYMD,
        title: '收货时间',
        field: 'arrival_time'
    },
    {
        ...tableColumnConfig.personName,
        title: '复核人',
        field: 'confirm_by'
    },
    {
        ...tableColumnConfig.dateYMD,
        title: '复核时间',
        field: 'confirm_time'
    }
];
