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
        title: '验货人',
        field: 'inspection_by'
    },
    {
        ...tableColumnConfig.dateYMD,
        title: '验货时间',
        field: 'inspection_time'
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
