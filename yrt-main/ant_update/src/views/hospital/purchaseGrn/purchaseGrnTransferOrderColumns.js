import tableColumnConfig from '@/config/tableColumnConfig';

// 收货单明细产品列表columns
export default [
    {
        ...tableColumnConfig.sn,
        title: '单据编号',
        field: 'voucher_sn'
    },
    {
        ...tableColumnConfig.organization,
        title: '机构名称'
    },
    {
        ...tableColumnConfig.personName,
        title: '经手人',
        field: 'handler_name'
    }
];
