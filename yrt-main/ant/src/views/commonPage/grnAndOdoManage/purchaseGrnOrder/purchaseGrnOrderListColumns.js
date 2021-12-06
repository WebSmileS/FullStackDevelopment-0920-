import tableColumnConfig from '@/config/tableColumnConfig';

// 收货单列表columns
export default [
    {
        ...tableColumnConfig.sn,
        field: 'grn_sn'
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
        title: '经手人',
        field: 'handler_name'
    },
    {
        ...tableColumnConfig.personName,
        title: '采购员',
        field: 'buyer_name'
    },
    {
        ...tableColumnConfig.personName,
        title: '仓库人员',
        field: 'warehouse_operator_name'
    },
    {
        ...tableColumnConfig.personName,
        title: '制单人',
        field: 'making_people_name'
    },
    {
        ...tableColumnConfig.dateYMDHMS,
        title: '入库时间',
        field: 'in_time'
    }
];
